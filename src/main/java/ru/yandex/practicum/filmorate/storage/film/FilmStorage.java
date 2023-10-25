package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();
    List<Film> findAllByYear(int year);

    List<Film> findAllByGenre(int genreId);

    List<Film> findAllByGenreAndYear(int genreId, int year);

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(Integer id);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

}
