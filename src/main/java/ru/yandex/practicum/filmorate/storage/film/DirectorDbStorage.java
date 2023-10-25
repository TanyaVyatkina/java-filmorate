package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DirectorDbStorage implements DirectorStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DirectorDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAllDirectors() {
        String sql = "select * from directors";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = "insert into directors (director_name) values (:director_name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, new MapSqlParameterSource().addValues(toMap(director)),
                keyHolder, new String[]{"director_id"});

        int id = keyHolder.getKey().intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sqlQuery = "update directors set director_name = :director_name where director_id = :director_id";
        jdbcTemplate.update(sqlQuery, toMap(director));
        return findDirectorById(director.getId()).get();
    }

    @Override
    public void removeDirectorById(Integer id) {
        String sqlQuery = "delete from directors where director_id = :director_id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("director_id", id);

        jdbcTemplate.update(sqlQuery, namedParameters);
    }

    @Override
    public Optional<Director> findDirectorById(Integer id) {
        String sqlQuery = "select * from directors where director_id = :director_id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("director_id", id);
        List<Director> users = jdbcTemplate.query(sqlQuery, namedParameters, (rs, rowNum) -> makeDirector(rs));
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    @Override
    public List<Director> findDirectorsByIdList(List<Integer> ids) {
        String sql = "select * from directors where director_id in (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeDirector(rs));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director(
                rs.getInt("director_id"),
                rs.getString("director_name")
        );
        return director;
    }

    private Map<String, Object> toMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("director_name", director.getName());
        if (director.getId() != null) {
            values.put("director_id", director.getId());
        }
        return values;
    }
}
