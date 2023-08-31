package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private int filmId = 0;
    private ValidateService validateService = new ValidateService();

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateService.validateFilm(film);
        film.setId(getFilmId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateService.validateFilm(film);
        Integer id = film.getId();
        validateService.validateUpdateFilm(id, films);
        films.put(id, film);
        log.debug("Обновлен фильм с id = : {}", id);
        return film;
    }

    private int getFilmId() {
        return ++filmId;
    }
}
