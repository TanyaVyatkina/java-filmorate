package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Поиск всех фильмов");
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") Integer id) {
        log.debug("Поиск фильма с id = {}", id);
        Film film = filmService.findFilmById(id);
        log.debug("Найден фильм {}.", film);
        return film;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("Пришел запрос на создание фильма.");
        Film createdFilm = filmService.createFilm(film);
        log.debug("Добавлен фильм c id = {}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.debug("Пришел запрос на обновление фильма.");
        Film updatedFilm = filmService.updateFilm(film);
        log.debug("Обновлен фильм с id = {}", film.getId());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.debug("Пришел запрос на добавление like к фильму.");
        filmService.addLike(id, userId);
        log.debug("Добавлен like к фильму (id = {}) пользователя (id = {}).", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.debug("Пришел запрос на удаление like к фильму");
        filmService.removeLike(id, userId);
        log.debug("Удален like к фильму (id = {}) пользователя (id = {}).", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        log.debug("Пришел запрос на поиск {} самых популярных фильмов.", count);
        List<Film> films = filmService.getMostPopularFilms(count);
        log.debug("Список самых популярных фильмов: {}.", films);
        return films;
    }
}
