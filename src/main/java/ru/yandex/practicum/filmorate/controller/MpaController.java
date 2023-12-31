package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> findAll() {
        log.debug("Поиск всех рейтингов.");
        return mpaService.findAllRatings();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable("id") Integer id) {
        log.debug("Поиск рейтинга с id = {}.", id);
        Mpa mpa = mpaService.findRatingById(id);
        log.debug("Найден рейтинг {}.", mpa);
        return mpa;
    }
}
