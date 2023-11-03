package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.film.ReviewService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> findReviews(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(required = false) Integer filmId) {
        log.debug("Пришёл запрос на поиск отзывов. {} количество {} ID фильма", count, filmId);
        List<Review> reviews = reviewService.findReviews(count, filmId);
        log.debug("Список найденных отзывов: {}", reviews);
        return reviews;
    }

    @PostMapping
    public Review create(@RequestBody Review review) {
        log.debug("Пришёл запрос на создание отзыва");
        Review createdReview = reviewService.createReview(review);
        log.debug("Добавлен отзыв с id = {}", createdReview.getReviewId());
        return createdReview;
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable("id") Integer id) {
        log.debug("Поиск отзыва с id = {}", id);
        Review review = reviewService.findReviewById(id);
        log.debug("Найден отзыв {}", review);
        return review;
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        log.debug("Пришёл запрос на обновление отзыва");
        Review updatedReview = reviewService.updateReview(review);
        log.debug("Обновлён отзыв с id = {}", review.getReviewId());
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        log.debug("Удаление отзыва с id = {}", id);
        reviewService.removeReviewById(id);
        log.debug("Отзыв удалён.");
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.debug("Пришёл запрос на добавление лайка отзыву");
        reviewService.addLike(id, userId);
        log.debug("Добавлен лайк отзыву (id = {}) от пользователя (id = {})", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.debug("Пришёл запрос на добавление дизлайка отзыву");
        reviewService.addDislike(id, userId);
        log.debug("Добавлен дизлайк отзыву (id = {}) от пользователя (id = {})", id, userId);
    }
}
