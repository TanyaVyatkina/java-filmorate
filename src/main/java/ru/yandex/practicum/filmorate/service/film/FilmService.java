package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private ValidateService validateService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, ValidateService validateService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validateService = validateService;
    }

    public Film findFilmById(Integer id) {
        Optional<Film> film = filmStorage.findById(id);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }
        return film.get();
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film updateFilm(Film film) {
        checkFilmExist(film.getId());
        return filmStorage.update(film);
    }

    public Film createFilm(Film film) {
        validateService.validateFilm(film);

        return filmStorage.create(film);
    }

    public void addLike(Integer id, Integer userId) {
        checkFilmExist(id);
        checkUserExist(userId);

        filmStorage.addLike(id, userId);
    }

    public void removeLike(Integer id, Integer userId) {
        checkFilmExist(id);
        checkUserExist(userId);

        filmStorage.removeLike(id, userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilmExist(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Не задан id фильма.");
        }
        if (filmStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден.");
        }
    }

    private void checkUserExist(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Не задан id пользователя.");
        }
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }
}
