package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> findAll() {
        log.debug("Поиск всех жанров.");
        return genreService.findAllGenres();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable("id") Integer id) {
        log.debug("Поиск жанра с id = {}.", id);
        Genre genre = genreService.findGenreById(id);
        log.debug("Найден жанр {}.", genre);
        return genre;
    }
}
