package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
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

@Component("dbFilmStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);
        saveFilmGenres(id, film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, likes_count = ?, rating_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getLikesCount(),
                film.getMpa().getId(),
                film.getId());
        updateFilmGenres(film);
        return findById(film.getId()).get()
                ;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        String sqlQuery = "select * from films where film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {

            Film film = new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    filmRows.getInt("likes_count"),
                    new Mpa(filmRows.getInt("rating_id"), null),
                    new LinkedHashSet<>()
            );
            fillGenres(film);
            fillMpa(film);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "insert into likes (user_id, film_id) " + "values (?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "delete from likes where user_id = ? and film_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), film.getId());
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "select * from genres order by genre_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        String sqlQuery = "select * from genres where genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name")
            );
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findAllRatings() {
        String sql = "select * from ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Optional<Mpa> findRatingById(Integer id) {
        String sqlQuery = "select * from ratings where rating_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("rating_id"), mpaRows.getString("name")
            );
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }

    public Set<Integer> getLikesByFilmId(Integer filmId) {
        String sql = "select user_id from likes where film_id = ?";

        Set<Integer> userIds = new HashSet<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> userIds.add(rs.getInt("user_id")), filmId);

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
                new Mpa(rs.getInt("rating_id"), null),
                new LinkedHashSet<>()
        );
        fillGenres(film);
        fillMpa(film);
        return film;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
        return genre;
    }

    private Mpa makeRating(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa(
                rs.getInt("rating_id"),
                rs.getString("name")
        );
        return mpa;
    }

    private void fillGenres(Film film) {
        String sql = "select * from genres where genre_id in " +
                "(select genre_id from film_genre where film_id = ?) order by genre_id";

        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), film.getId());
        film.setGenres(new HashSet<>(genres));
    }

    private void fillMpa(Film film) {
        Mpa mpa = findRatingById(film.getMpa().getId()).get();
        film.setMpa(mpa);
    }

    private void saveFilmGenres(int id, Set<Genre> genres) {
        if (genres != null) {
            String sqlQuery = "insert into film_genre(film_id, genre_id) " +
                    "values (?, ?)";
            for (Genre genre : genres) {
                jdbcTemplate.update(sqlQuery,
                        id,
                        genre.getId());
            }
        }
    }

    private void updateFilmGenres(Film film) {
        int filmId = film.getId();

        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);

        saveFilmGenres(filmId, film.getGenres());
    }
}
