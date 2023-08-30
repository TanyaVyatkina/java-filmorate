package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getFilmId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);
        int id = film.getId();
        if (films.get(id) == null) {
            log.warn("Фильм с id = " + id + " не найден");
            throw new IllegalArgumentException("Фильм с id = " + id + " не найден.");
        }
        films.put(id, film);
        log.debug("Обновлен фильм с id = : {}", film.getId());
        return film;
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.warn("Фильм не задан.");
            throw new IllegalArgumentException("Фильм не задан.");
        }
        String error = null;
        if (film.getName() == null || film.getName().isEmpty()) {
            error = "Название фильма не может быть пустым.";
        } else if (film.getDescription() != null && film.getDescription().length() > 200) {
            error = "Максимальная длина описания не должна превышать 200 символов.";
        } else if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            error = "Дата релиза должна быть не раньше 28 декабря 1895 года.";
        } else if (film.getDuration() <= 0) {
            error = "Продолжительность фильма должна быть положительным числом.";
        }
        if (error != null) {
            log.warn(error);
            throw new ValidationException(error);
        }
    }

    private int getFilmId() {
        return ++filmId;
    }
}
