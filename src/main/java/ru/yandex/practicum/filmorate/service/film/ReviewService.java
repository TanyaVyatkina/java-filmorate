package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Review> findReviews(Integer count, Integer filmId) {
        if (filmId != null) {
            filmStorage.findById(filmId)
                    .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));

            return reviewStorage.findReviewsByFilmIdWithCount(count, filmId);
        }

        return reviewStorage.findReviewsWithCount(count);
    }

    public Review createReview(Review review) {
        validateReview(review);
        return reviewStorage.createReview(review);
    }

    public Review findReviewById(Integer id) {
        return reviewStorage.findReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден."));
    }

    public Review updateReview(Review review) {
        checkReviewExists(review.getReviewId());
        validateReview(review);
        return reviewStorage.updateReview(review);
    }

    public void removeReviewById(Integer id) {
        reviewStorage.removeReviewById(id);
    }

    public void addLike(int reviewId, int userId) {
        checkReviewExists(reviewId);

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        checkReviewExists(reviewId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        reviewStorage.addDislike(reviewId, userId);
    }

    private void validateReview(Review review) {
        if (review.getUseful() == null) {
            review.setUseful(0);
        }

        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Текст отзыва не может быть пустым.");
        }

        if (review.getIsPositive() == null) {
            throw new ValidationException("Отзыв не может быть без типа.");
        }

        if (review.getUserId() == null) {
            throw new ValidationException("Отзыв не может быть создан не без пользователя.");
        }

        if (review.getFilmId() == null) {
            throw new ValidationException("Отзыв не может не относиться ни к какому фильму.");
        }

        userStorage.findById(review.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + review.getUserId() + " не найден."));

        filmStorage.findById(review.getFilmId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + review.getFilmId() + " не найден."));
    }

    private void checkReviewExists(int reviewId) {
        reviewStorage.findReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + reviewId + " не найден."));
    }
}
