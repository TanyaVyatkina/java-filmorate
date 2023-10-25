package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
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
    public List<Film> findAllByYear(int year) {
        String sql =
                "select f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, f.likes_count, r.rating_name " +
                        "from films f join ratings r on f.rating_id = r.rating_id " +
                        "where year(f.release_date) = :release_date order BY f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("release_date", year);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        return films;
    }

    @Override
    public List<Film> findAllByGenre(int genreId) {
        String sql =
                "select f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, f.likes_count, r.rating_name " +
                        "from films f join ratings r on f.rating_id = r.rating_id " +
                        "where f.film_id in (select film_genre.film_id from film_genre where genre_id = :genre_id ) order by f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("genre_id", genreId);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        return films;
    }

    @Override
    public List<Film> findAllByGenreAndYear(int genreId, int year) {
        String sql =
                "select f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, f.likes_count, r.rating_name " +
                        "from films f join ratings r on f.rating_id = r.rating_id " +
                        "where f.film_id in (select film_genre.film_id from film_genre where genre_id = :genre_id) " +
                        "and year(f.release_date) = :release_date order by f.film_id";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("genre_id", genreId);
        parameters.put("release_date", year);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        return films;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "insert into films(name, description, release_date, duration, likes_count," +
                " rating_id) values (:name, :description, :release_date, :duration, :likes_count, :rating_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, new MapSqlParameterSource().addValues(toMap(film)),
                keyHolder, new String[]{"film_id"});

        int id = keyHolder.getKey().intValue();
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
        SqlParameterSource parameters = new MapSqlParameterSource("film_id", id);
        List<Film> films = jdbcTemplate.query(sqlQuery, parameters, (rs, rowNum) -> makeFilm(rs));
        if (films.isEmpty()) {
            return Optional.empty();
        }
        fillGenres(films);
        return Optional.of(films.get(0));
    }

    @Override
    public void addLike(Film film, User user) {
        changeLikesCount(film, true);

        String sqlQuery = "merge into likes as l using " +
                "(select cast(:user_id as int) as user_id, cast(:film_id as int) as film_id) as lk " +
                "on l.user_id = lk.user_id and l.film_id = lk.film_id " +
                "when not matched then insert (user_id, film_id) values (:user_id, :film_id)";
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
        return film;
    }

    private void fillGenres(List<Film> films) {
        Map<Integer, Film> filmsMap = films
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        String sql = "select f.film_id, g.genre_id, g.name from genres as g join film_genre as f " +
                "on g.genre_id = f.genre_id " +
                "where film_id in (:film_ids) order by genre_id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("film_ids", filmsMap.keySet());
        jdbcTemplate.query(sql, namedParameters, (ResultSetExtractor<Void>) rs -> fillGenres(rs, filmsMap));
    }

    private void saveFilmGenres(int id, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        StringBuilder sqlBuilder = new StringBuilder("insert into film_genre(film_id, genre_id) values ");
        String[] values = new String[genres.size()];
        String genreIdParam = "genre_id";
        int i = 0;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_id", id);
        for (Genre genre : genres) {
            values[i] = "(:film_id, :genre_id" + i + ")";
            parameters.put(genreIdParam + i, genre.getId());
            i++;
        }
        sqlBuilder.append(String.join(", ", values));
        jdbcTemplate.update(sqlBuilder.toString(), parameters);
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
        int filmId = film.getId();
        String sqlQuery = "select count(user_id) from likes where film_id = :film_id";

        SqlParameterSource parameter = new MapSqlParameterSource("film_id", filmId);
        int likes = jdbcTemplate.queryForObject(sqlQuery, parameter, Integer.class);

        likes = increase ? ++likes : --likes;

        sqlQuery = "update films set likes_count = :likes_count where film_id = :film_id";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("likes_count", likes);
        parameters.put("film_id", filmId);
        jdbcTemplate.update(sqlQuery, parameters);
    }

    private Void fillGenres(ResultSet rs, Map<Integer, Film> films)
            throws SQLException, DataAccessException {
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            films.get(filmId).addGenre(genre);
        }
        return null;
    }
}