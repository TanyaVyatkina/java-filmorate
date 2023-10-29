package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class ReviewDbStorage implements ReviewStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReviewDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> findAllReviews() {
        String sql = "select * from reviews order by useful desc";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public List<Review> findReviewsWithCount(Integer count) {
        String sql = "select * from reviews order by useful desc limit :count";
        SqlParameterSource namedParameters = new MapSqlParameterSource("count", count);

        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public List<Review> findReviewsByFilmIdWithCount(Integer count, Integer filmId) {
        String sql = "select * from reviews where filmId = :filmId order by useful desc limit :count";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("filmId", filmId);
        mapSqlParameterSource.addValue("count", count);

        return jdbcTemplate.query(sql, mapSqlParameterSource, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public Review createReview(Review review) {
        String sqlQuery = "insert into reviews(content, isPositive, userId, filmId, useful) values (:content, :isPositive, :userId, :filmId, :useful)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, new MapSqlParameterSource().addValues(toMap(review)),
                keyHolder, new String[]{"reviewId"});

        int id = keyHolder.getKey().intValue();
        review.setReviewId(id);
        return review;
    }

    @Override
    public Optional<Review> findReviewById(Integer id) {
        String sqlQuery = "select * from reviews where reviewId = :reviewId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("reviewId", id);
        List<Review> reviews = jdbcTemplate.query(sqlQuery, namedParameters, (rs, rowNum) -> makeReview(rs));
        if (reviews.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(reviews.get(0));
        }
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "update reviews set content = :content, isPositive = :isPositive where reviewId = :reviewId";
        jdbcTemplate.update(sqlQuery, toMap(review));
        return findReviewById(review.getReviewId()).get();
    }

    @Override
    public void removeReviewById(Integer id) {
        String sqlQuery = "delete from reviews where reviewId = :reviewId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("reviewId", id);

        jdbcTemplate.update(sqlQuery, namedParameters);
    }

    public void addLike(Integer reviewId, Integer userId) {
        String sqlQuery = "update reviews set useful = useful + 1 where reviewId = :reviewId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("reviewId", reviewId);

        jdbcTemplate.update(sqlQuery, namedParameters);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        String sqlQuery = "update reviews set useful = useful - 1 where reviewId = :reviewId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("reviewId", reviewId);

        jdbcTemplate.update(sqlQuery, namedParameters);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        Review review = new Review(
                rs.getInt("reviewId"),
                rs.getString("content"),
                rs.getBoolean("isPositive"),
                rs.getInt("userId"),
                rs.getInt("filmId"),
                rs.getInt("useful")
        );

        return review;
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        if (review.getReviewId() != null) {
            values.put("reviewId", review.getReviewId());
        }

        values.put("content", review.getContent());
        values.put("isPositive", review.getIsPositive());
        values.put("userId", review.getUserId());
        values.put("filmId", review.getFilmId());
        values.put("useful", review.getUseful());

        return values;
    }
}