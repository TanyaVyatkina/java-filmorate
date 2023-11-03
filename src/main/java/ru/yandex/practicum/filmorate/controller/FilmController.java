package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SearchType;
import ru.yandex.practicum.filmorate.model.enums.SortingType;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.ArrayList;
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
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        log.debug("Пришел запрос на поиск самых популярных фильмов. {} количество {} ID жанра {} год", count);
        List<Film> films = filmService.getMostPopularFilms(count, genreId, year);
        log.debug("Список самых популярных фильмов {}", films);
        return films;
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmsByDirectorId(@PathVariable("id") Integer id, @RequestParam String sortBy) {
        log.debug("Пришел запрос на поиск фильмов режиссера с id = {}, сортировка по {}", id, sortBy);
        List<Film> films;
        try {
            films = filmService.getFilmsByDirectorId(id, SortingType.valueOf(sortBy.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            log.debug("Неверно указан параметр сортировки.");
            throw new ValidationException("Неверно указан параметр сортировки.");
        }
        log.debug("Найдены фильмы: {}.", films);
        return films;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam("userId") final Integer userId,
                                     @RequestParam("friendId") final Integer friendId) {
        log.debug("Пришел запрос на поиск общих фильмов пользователей: {},  {}", userId, friendId);
        List<Film> films = filmService.getCommonFilms(userId, friendId);
        log.debug("Найдены фильмы: {}.", films);
        return films;
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.debug("Поиск фильмов по запросам {}, {}", query, by);
        List<Film> films = filmService.searchFilms(query, stringToSearchType(by));
        log.debug("Найдены фильмы: {}.", films);
        return films;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") final Integer filmId) {
        filmService.deleteById(filmId);
        log.debug("Фильм с id = {} удалён", filmId);
    }

    private List<SearchType> stringToSearchType(String str) {
        String[] byArray = str.split(",");
        List<SearchType> byList = new ArrayList<>();
        try {
            for (int i = 0; i < byArray.length; i++) {
                byList.add(SearchType.valueOf(byArray[i].toUpperCase()));
            }
        } catch (IllegalArgumentException ex) {
            log.debug("Неверно указаны параметры поиска.");
            throw new ValidationException("Неверно указаны параметры поиска.");
        }
        return byList;
    }

}
