package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private ValidateService validateService;

    @Autowired
    public FilmService(FilmStorage filmStorage, ValidateService validateService) {
        this.filmStorage = filmStorage;
        this.validateService = validateService;
    }

    public Film findFilmById(Integer id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }
        return film;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film updateFilm(Film film) {
        validateService.validateFilmExist(film.getId());
        return filmStorage.update(film);
    }

    public Film createFilm(Film film) {
        validateService.validateFilm(film);
        return filmStorage.create(film);
    }

    public void addLike(Integer id, Integer userId) {
        validateService.validateUserExist(userId);
        Film film = validateService.validateFilmExist(id);

        film.addLike(userId);
        filmStorage.update(film);
    }

    public void removeLike(Integer id, Integer userId) {
        validateService.validateUserExist(userId);
        Film film = validateService.validateFilmExist(id);

        film.removeLike(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        if (film2.getLikes() != null && film1.getLikes() != null) {
            return film2.getLikes().size() - film1.getLikes().size();
        }
        if (film2.getLikes() != null) {
            return 1;
        }
        if (film1.getLikes() != null) {
            return -1;
        }
        return 0;
    }
}
