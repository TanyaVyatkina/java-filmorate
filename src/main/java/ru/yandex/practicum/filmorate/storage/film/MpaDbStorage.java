package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MpaDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAllRatings() {
        String sql = "select * from ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
    }

    @Override
    public Optional<Mpa> findRatingById(Integer id) {
        String sqlQuery = "select * from ratings where rating_id = :rating_id";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, Collections.singletonMap("rating_id", id));
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("rating_id"), mpaRows.getString("rating_name")
            );
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }

    private Mpa makeRating(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa(
                rs.getInt("rating_id"),
                rs.getString("rating_name")
        );
        return mpa;
    }
}
