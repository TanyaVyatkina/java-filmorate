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

    @GetMapping(value = "/popular", params = {})
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("Пришел запрос на поиск самых популярных фильмов. {} количество {} ID жанра {} год", count);
        List<Film> films = filmService.getMostPopularFilms(count);
        log.debug("Список самых популярных фильмов {}", films);
        return films;
    }

    @GetMapping(value = "/popular", params = { "year" })
    public List<Film> getMostPopularFilmsByYear(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "year") Integer year) {
        log.debug("Пришел запрос на поиск самых популярных фильмов. {} количество {} ID жанра {} год", count, year);
        List<Film> films = filmService.getMostPopularFilmsByYear(count, year);
        log.debug("Список самых популярных фильмов {} по годам {}", films, year);
        return films;
    }

    @GetMapping(value = "/popular", params = { "genreId" })
    public List<Film> getMostPopularFilmsByGenre(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId") Integer genreId) {
        log.debug("Пришел запрос на поиск самых популярных фильмов. {} количество {} ID жанра ", count, genreId);
        List<Film> films = filmService.getMostPopularFilmsByGenre(count, genreId);
        log.debug("Список самых популярных фильмов {} по годам {}", films, genreId);
        return films;
    }

    @GetMapping(value = "/popular", params = { "year", "genreId" })
    public List<Film> getMostPopularFilmsByGenreAndYear(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId") Integer genreId,
            @RequestParam(value = "year") Integer year) {
        log.debug("Пришел запрос на поиск самых популярных фильмов. {} количество {} ID жанра {} год", count, genreId, year);
        List<Film> films = filmService.getMostPopularFilmsByGenreAndYear(count, genreId, year);
        log.debug("Список самых популярных фильмов {} по годам {}", films, genreId, year);
        return films;
    }

        /**
        if (!genreId.equals(null) && !year.equals(null)) {
            films.addAll(filmService.getMostPopularFilmsByGenreAndYear(count, genreId, year));
            log.debug("Список самых популярных фильмов: {}.", films);
        }
        if (genreId.equals(null) && year.equals(null)) {
            System.out.println(count);
            films.addAll(films = filmService.getMostPopularFilms(count));
            log.debug("Список самых популярных фильмов по жанрам и годам: {}. {} {}", films, genreId, year);
            return films;
        }
        if (genreId.equals(null)) {
            films.addAll(films = filmService.getMostPopularFilmsByYear(count, year));
            log.debug("Список самых популярных фильмов по годам: {}. {}", films, year);
            return films;
        }
        if (year.equals(null)) {
            films.addAll(films = filmService.getMostPopularFilmsByGenre(count, genreId));
            log.debug("Список самых популярных фильмов по жанрам: {}. {}", films, genreId);
            return films;
        }
        return films;
    }
         **/

    @GetMapping("/director/{id}")
    public List<Film> getFilmsByDirectorId(@PathVariable("id") Integer id, @RequestParam String sortBy) {
        log.debug("Пришел запрос на поиск фильмов режиссера с id = {}, сортировка по {}", id, sortBy);
        List<Film> films = filmService.getFilmsByDirectorId(id, sortBy);
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
        List<Film> films = filmService.searchFilms(query, by);
        log.debug("Найдены фильмы: {}.", films);
        return films;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") final Integer filmId) {
        filmService.deleteById(filmId);
        log.debug("Фильм с id = {} удалён", filmId);
    }

}
