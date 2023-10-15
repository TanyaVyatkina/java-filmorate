package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GenreDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "select * from genres order by genre_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        String sqlQuery = "select * from genres where genre_id = :genre_id";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, Collections.singletonMap("genre_id", id));
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
    public List<Genre> findGenresByIdList(List<Integer> ids) {
        String sql = "select * from genres where genre_id in (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
        return genre;
    }
}
