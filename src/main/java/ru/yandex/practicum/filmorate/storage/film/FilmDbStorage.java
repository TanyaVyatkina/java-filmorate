package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("dbFilmStorage")
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FilmDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films as f join ratings as r on f.rating_id = r.rating_id ";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        return films;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();
        film.setId(id);
        saveFilmGenres(id, film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set " +
                "name = :name, description = :description, release_date = :release_date, duration = :duration, " +
                "likes_count = :likes_count, rating_id = :rating_id " +
                "where film_id = :film_id";
        jdbcTemplate.update(sqlQuery, toMap(film));
        updateFilmGenres(film);
        return findById(film.getId()).get();
    }

    @Override
    public Optional<Film> findById(Integer id) {
        String sqlQuery = "select * from films as f left join ratings as r on f.rating_id = r.rating_id " +
                "where film_id = :film_id";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, Collections.singletonMap("film_id", id));
        if (filmRows.next()) {

            Film film = new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    filmRows.getInt("likes_count"),
                    new Mpa(filmRows.getInt("rating_id"), filmRows.getString("rating_name")),
                    new LinkedHashSet<>()
            );
            fillGenres(List.of(film));
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(Film film, User user) {
        changeLikesCount(film, true);

        String sqlQuery = "insert into likes (user_id, film_id) " + "values (:user_id, :film_id)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("film_id", film.getId());
        jdbcTemplate.update(sqlQuery, parameters);
    }

    @Override
    public void removeLike(Film film, User user) {
        changeLikesCount(film, false);

        String sqlQuery = "delete from likes where user_id = :user_id and film_id = :film_id";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("film_id", film.getId());
        jdbcTemplate.update(sqlQuery, parameters);
    }

    public Set<Integer> getLikesByFilmId(Integer filmId) {
        String sql = "select user_id from likes where film_id = :film_id";

        Set<Integer> userIds = new HashSet<>();
        SqlParameterSource namedParameters = new MapSqlParameterSource("film_id", filmId);

        jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> userIds.add(rs.getInt("user_id")));

        return userIds;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                rs.getInt("likes_count"),
                new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")),
                new LinkedHashSet<>()
        );
        //   fillMpa(film);
        return film;
    }

    private void fillGenres(List<Film> films) {
        List<Integer> filmIds = films
                .stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        String sql = "select f.film_id, g.genre_id, g.name from genres as g join film_genre as f " +
                "on g.genre_id = f.genre_id " +
                "where film_id in (:film_ids) order by genre_id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("film_ids", filmIds);
        Map<Integer, Set<Genre>> genres = jdbcTemplate.query(sql, namedParameters, this::extractGenreData);
        for (Film film : films) {
            Set<Genre> g = genres.get(film.getId());
            if (g == null) {
                film.setGenres(Collections.EMPTY_SET);
            } else {
                film.setGenres(genres.get(film.getId()));
            }
        }
    }

    private void saveFilmGenres(int id, Set<Genre> genres) {
        if (genres != null) {
            String sqlQuery = "insert into film_genre(film_id, genre_id) " +
                    "values (:film_id, :genre_id)";
            for (Genre genre : genres) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("film_id", id);
                parameters.put("genre_id", genre.getId());
                jdbcTemplate.update(sqlQuery, parameters);
            }
        }
    }

    private void updateFilmGenres(Film film) {
        int filmId = film.getId();

        String sqlQuery = "delete from film_genre where film_id = :film_id";
        jdbcTemplate.update(sqlQuery, Collections.singletonMap("film_id", filmId));

        saveFilmGenres(filmId, film.getGenres());
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("likes_count", film.getLikesCount());
        values.put("rating_id", film.getMpa().getId());
        if (film.getId() != null) {
            values.put("film_id", film.getId());
        }
        return values;
    }

    private void changeLikesCount(Film film, boolean increase) {
        int likes = film.getLikesCount();
        likes = increase ? ++likes : --likes;

        String sqlQuery = "update films set likes_count = :likes_count where film_id = :film_id";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("likes_count", likes);
        parameters.put("film_id", film.getId());
        jdbcTemplate.update(sqlQuery, parameters);
    }

    private Map<Integer, Set<Genre>> extractGenreData(ResultSet rs)
            throws SQLException, DataAccessException {
        Map<Integer, Set<Genre>> data = new HashMap<>();
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            data.putIfAbsent(filmId, new LinkedHashSet<>());
            data.get(filmId).add(genre);
        }
        return data;
    }
}