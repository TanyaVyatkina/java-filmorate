package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    List<Review> findAllReviews();

    List<Review> findReviewsWithCount(Integer count);

    List<Review> findReviewsByFilmIdWithCount(Integer count, Integer filmId);

    Review createReview(Review review);

    Review updateReview(Review review);

    Optional<Review> findReviewById(Integer id);

    void removeReviewById(Integer id);

    void addLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);
}
