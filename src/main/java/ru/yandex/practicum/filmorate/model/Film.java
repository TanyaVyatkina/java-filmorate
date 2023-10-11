package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int likesCount;
    private Mpa mpa;
    private Set<Genre> genres;
}
