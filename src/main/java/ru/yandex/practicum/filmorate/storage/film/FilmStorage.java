package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(Integer id);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    List<Genre> findAllGenres();

    Optional<Genre> findGenreById(Integer id);

    List<Mpa> findAllRatings();

    Optional<Mpa> findRatingById(Integer id);
}
