package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Mpa> findAll() {
        log.debug("Поиск всех рейтингов.");
        return filmService.findAllRatings();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable("id") Integer id) {
        log.debug("Поиск рейтинга с id = {}.", id);
        Mpa mpa = filmService.findRatingById(id);
        log.debug("Найден рейтинг {}.", mpa);
        return mpa;
    }
}
