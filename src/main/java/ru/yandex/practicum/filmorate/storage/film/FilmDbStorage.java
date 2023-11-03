package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.SearchType;
import ru.yandex.practicum.filmorate.model.enums.SortingType;

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
        fillDirectors(films);
        return films;
    }

    @Override
    public Film create(Film film) {
        String sql = "insert into films(name, description, release_date, duration, likes_count," +
                " rating_id) values (:name, :description, :release_date, :duration, :likes_count, :rating_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource().addValues(toMap(film)),
                keyHolder, new String[]{"film_id"});

        int id = keyHolder.getKey().intValue();
        film.setId(id);
        saveFilmGenres(id, film.getGenres());
        saveFilmDirectors(id, film.getDirectors());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "update films set " +
                "name = :name, description = :description, release_date = :release_date, duration = :duration, " +
                "likes_count = :likes_count, rating_id = :rating_id " +
                "where film_id = :film_id";
        jdbcTemplate.update(sql, toMap(film));
        updateFilmGenres(film);
        updateFilmDirectors(film);
        return findById(film.getId()).get();
    }

    @Override
    public Optional<Film> findById(Integer id) {
        String sql = "select * from films as f left join ratings as r on f.rating_id = r.rating_id " +
                "where film_id = :film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("film_id", id);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        if (films.isEmpty()) {
            return Optional.empty();
        }
        fillGenres(films);
        fillDirectors(films);
        return Optional.of(films.get(0));
    }

    @Override
    public void addLike(Film film, User user) {
        changeLikesCount(film, true);

        String sql = "merge into likes as l using " +
                "(select cast(:user_id as int) as user_id, cast(:film_id as int) as film_id) as lk " +
                "on l.user_id = lk.user_id and l.film_id = lk.film_id " +
                "when not matched then insert (user_id, film_id) values (:user_id, :film_id)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("film_id", film.getId());
        jdbcTemplate.update(sql, parameters);
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

    @Override
    public List<Film> getFilmsByDirectorId(Integer directorId, SortingType sortBy) {
        List<Film> films;
        if (SortingType.LIKES.equals(sortBy)) {
            films = getSortedFilmsByLikes(directorId);
        } else {
            films = getSortedFilmsByYear(directorId);
        }
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    @Override
    public List<Film> getMostPopularFilmsByYear(Integer count, Integer year) {
        String sql = "select * from films f " +
                "left join ratings as r on f.rating_id = r.rating_id " +
                "left join likes as l on f.film_id = l.film_id " +
                "where year(f.release_date) = :year " +
                "group by f.film_id " +
                "order by count(l.user_id) desc limit :limit";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("year", year);
        parameters.put("limit", count);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    @Override
    public List<Film> getMostPopularFilmsByGenre(Integer count, Integer genreId) {
        String sql = "select * from film_genre fg " +
                "right join films f on f.film_id = fg.film_id " +
                "left join ratings as r on f.rating_id = r.rating_id " +
                "left join likes as l on f.film_id = l.film_id " +
                "where fg.genre_id = :genreId " +
                "group by f.film_id " +
                "order by count(l.user_id) desc limit :limit";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("genreId", genreId);
        parameters.put("limit", count);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    @Override
    public List<Film> getMostPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {

        String sql = "select * from film_genre as fg " +
                "right join films f on f.film_id = fg.film_id " +
                "left join ratings as r on f.rating_id = r.rating_id " +
                "left join likes as l on f.film_id = l.film_id " +
                "where fg.genre_id = :genreId and year(f.release_date) = :year " +
                "group by f.film_id " +
                "order by count(l.user_id) desc limit :limit";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("year", year);
        parameters.put("genreId", genreId);
        parameters.put("limit", count);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        String sql = "select * from films as f left join likes as l on f.film_id = l.film_id " +
                "left join ratings as r on f.rating_id = r.rating_id " +
                "group by f.film_id, l.film_id in ( select film_id from likes ) " +
                "order by count(l.user_id) desc limit :limit";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("limit", count);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }


    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, count(lv1.film_id) as likes_count " +
                "from films as f " +
                "join ratings as r on f.rating_id = r.rating_id " +
                "join likes lv1 on f.film_id = lv1.film_id " +
                "join likes lv2 on f.film_id = lv2.film_id " +
                "where lv1.user_id = :userId and lv2.user_id = :friendId " +
                "group by f.film_id " +
                "order by likes_count desc";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        List<Film> films = jdbcTemplate.query(sql, params, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    @Override
    public List<Film> searchFilms(String query, List<SearchType> by) {
        String queryForSql = "%" + query.toLowerCase() + "%";
        String sql;
        if (by.containsAll(List.of(SearchType.TITLE, SearchType.DIRECTOR))) {
            sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, f.likes_count, r.rating_name " +
                    "from films as f left join ratings as r on f.rating_id = r.rating_id " +
                    "left join film_director as fd on f.film_id = fd.film_id " +
                    "left join directors as d on d.director_id = fd.director_id " +
                    "where lower(d.director_name) like :name or lower(f.name) like :name order by f.release_date desc";
        } else if (by.contains(SearchType.TITLE)) {
            sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, f.likes_count, r.rating_name " +
                    "from films as f join ratings as r on f.rating_id = r.rating_id " +
                    "where lower(f.name) like :name";
        } else {
            sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, f.likes_count, r.rating_name " +
                    "from films as f join ratings as r on f.rating_id = r.rating_id " +
                    "join film_director as fd on f.film_id = fd.film_id " +
                    "join directors d on d.director_id = fd.director_id " +
                    "where lower(d.director_name) like :name";
        }
        SqlParameterSource parameters = new MapSqlParameterSource("name", queryForSql);
        List<Film> foundFilms = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(foundFilms);
        fillDirectors(foundFilms);
        return foundFilms;
    }

    private List<Film> getSortedFilmsByYear(Integer directorId) {
        String sql = "select * from films as f left join film_director as fd on f.film_id = fd.film_id " +
                "left join ratings as r on f.rating_id = r.rating_id " +
                "where fd.director_id = :director_id order by f.release_date";
        SqlParameterSource parameters = new MapSqlParameterSource("director_id", directorId);
        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
    }

    private List<Film> getSortedFilmsByLikes(Integer directorId) {
        String sql = "select * from films as f left join film_director as fd on f.film_id = fd.film_id " +
                "left join likes as l on l.film_id = f.film_id " +
                "left join ratings as r on f.rating_id = r.rating_id " +
                "where fd.director_id = :director_id group by f.film_id order by count(l.user_id) desc";
        SqlParameterSource parameters = new MapSqlParameterSource("director_id", directorId);
        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
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
                new LinkedHashSet<>(), new LinkedHashSet<>()
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

    private void fillDirectors(List<Film> films) {
        Map<Integer, Film> filmsMap = films
                .stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        String sql = "select f.film_id, d.director_id, d.director_name from directors as d join film_director as f " +
                "on d.director_id = f.director_id " +
                "where film_id in (:film_ids)"; // order by director_id
        SqlParameterSource namedParameters = new MapSqlParameterSource("film_ids", filmsMap.keySet());
        jdbcTemplate.query(sql, namedParameters, (ResultSetExtractor<Void>) rs -> fillDirectors(rs, filmsMap));
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

    private void updateFilmDirectors(Film film) {
        int filmId = film.getId();

        String sqlQuery = "delete from film_director where film_id = :film_id";
        jdbcTemplate.update(sqlQuery, Collections.singletonMap("film_id", filmId));

        saveFilmDirectors(filmId, film.getDirectors());
    }

    private void saveFilmDirectors(int id, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        StringBuilder sqlBuilder = new StringBuilder("insert into film_director(film_id, director_id) values ");
        String[] values = new String[directors.size()];
        String directorIdParam = "director_id";
        int i = 0;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_id", id);
        for (Director director : directors) {
            values[i] = "(:film_id, :director_id" + i + ")";
            parameters.put(directorIdParam + i, director.getId());
            i++;
        }
        sqlBuilder.append(String.join(", ", values));
        jdbcTemplate.update(sqlBuilder.toString(), parameters);
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

    private Void fillDirectors(ResultSet rs, Map<Integer, Film> films)
            throws SQLException, DataAccessException {
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            Director director = new Director(rs.getInt("director_id"),
                    rs.getString("director_name"));
            films.get(filmId).addDirector(director);
        }
        return null;
    }

    private List<Film> findAllByYear(int year) {
        String sql =
                "select f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, f.likes_count, r.rating_name " +
                        "from films f join ratings r on f.rating_id = r.rating_id " +
                        "where year(f.release_date) = :release_date order BY f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("release_date", year);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    private List<Film> findAllByGenre(int genreId) {
        String sql =
                "select f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, f.likes_count, r.rating_name " +
                        "from films f join ratings r on f.rating_id = r.rating_id " +
                        "where f.film_id in (select film_id from film_genre where genre_id = :genre_id ) order by f.film_id";
        SqlParameterSource parameters = new MapSqlParameterSource("genre_id", genreId);
        List<Film> films = jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeFilm(rs));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }

    private List<Film> findAllByGenreAndYear(int genreId, int year) {
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
        fillDirectors(films);
        return films;
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from films where film_id = :film_id";
        jdbcTemplate.update(sql, Collections.singletonMap("film_id", id));
    }

    @Override
    public List<Film> findRecomendedFilms(Integer id) {

        List<Film> films = new ArrayList<>();
        Map<Integer, List<Integer>> filmsOfUsers = new HashMap<>();

        String sqlGetUser = "select l2.user_id as l2_user_id from likes as l2, likes as l1 " +
                "where l1.film_id = l2.film_id " +
                "and l1.user_id = :user_id and l1.user_id != l2.user_id " +
                "group by l1.user_id, l2.user_id " +
                "order by count(*) desc limit 1";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", id);
        List<Integer> recommendedUserId = jdbcTemplate.query(sqlGetUser, parameters, (rs, rowNum) -> rs.getInt("l2_user_id"));

        String findIdSql = "select * from films join ratings on films.rating_id = ratings.rating_id " +
                "where film_id in (select film_id from likes " +
                "where user_id in (:users_ids) " +
                "and film_id not in (select film_id from likes where user_id = :main_user_id))";
        Map<String, Object> parametersFilms = new HashMap<>();
        parameters.put("users_ids", recommendedUserId);
        parameters.put("main_user_id", id);
        films.addAll(jdbcTemplate.query(findIdSql, parameters, (rs, rowNum) -> makeFilm(rs)));
        fillGenres(films);
        fillDirectors(films);
        return films;
    }
}