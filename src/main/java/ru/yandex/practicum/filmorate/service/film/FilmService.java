package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private ValidateService validateService;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage,
                       @Qualifier("dbUserStorage") UserStorage userStorage,
                       ValidateService validateService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validateService = validateService;
    }

    public Film findFilmById(Integer id) {
        return findFilmIfExist(id);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film updateFilm(Film film) {
        validateService.validateUpdateFilm(film);
        findFilmIfExist(film.getId());

        return filmStorage.update(film);
    }

    public Film createFilm(Film film) {
        validateService.validateFilm(film);

        return filmStorage.create(film);
    }

    public void addLike(Integer id, Integer userId) {
        Film film = findFilmIfExist(id);
        User user = findUserIfExist(userId);

        int likes = film.getLikesCount();
        film.setLikesCount(++likes);

        filmStorage.update(film);
        filmStorage.addLike(film, user);
    }

    public void removeLike(Integer id, Integer userId) {
        Film film = findFilmIfExist(id);
        User user = findUserIfExist(userId);

        filmStorage.removeLike(film, user);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film findFilmIfExist(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден."));
    }

    private User findUserIfExist(Integer id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }
}
