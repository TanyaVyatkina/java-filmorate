package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.service.user.EventService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private final ValidateService validateService;
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public ReviewService(ValidateService validateService, ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage, EventService eventService) {
        this.validateService = validateService;
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
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
        Review newReview = reviewStorage.createReview(review);
        eventService.addEvent(new Event(EventType.REVIEW, EventOperation.ADD, newReview.getReviewId(), newReview.getUserId()));
        return newReview;
    }

    public Review findReviewById(Integer id) {
        return reviewStorage.findReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден."));
    }

    public Review updateReview(Review review) {
        validateReview(review);

        if (review.getReviewId() == null) {
            throw new ValidationException("Передан отзыв без id");
        }

        checkReviewExists(review.getReviewId());

        Review reviewUpdated = reviewStorage.updateReview(review);

        eventService.addEvent(new Event(EventType.REVIEW, EventOperation.UPDATE, reviewUpdated.getReviewId(), reviewUpdated.getUserId()));
        return reviewUpdated;
    }

    public void removeReviewById(Integer id) {
        reviewStorage.findReviewById(id).ifPresent((review) -> {
            reviewStorage.removeReviewById(id);
            eventService.addEvent(new Event(EventType.REVIEW, EventOperation.REMOVE, review.getReviewId(), review.getUserId()));
        });
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
        validateService.validateReview(review);

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
