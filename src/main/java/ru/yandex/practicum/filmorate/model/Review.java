package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {
    @EqualsAndHashCode.Include
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful = 0;

    public Review(Integer reviewId, String content, Boolean isPositive, Integer userId, Integer filmId, int useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
