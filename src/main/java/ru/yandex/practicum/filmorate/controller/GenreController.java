package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> findAll() {
        log.debug("Поиск всех жанров.");
        return filmService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable("id") Integer id) {
        log.debug("Поиск жанра с id = {}.", id);
        Genre genre = filmService.findGenreById(id);
        log.debug("Найден жанр {}.", genre);
        return genre;
    }
}
