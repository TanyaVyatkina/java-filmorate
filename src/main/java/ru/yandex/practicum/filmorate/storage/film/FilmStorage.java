package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
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

    List<Film> getFilmsByDirectorId(Integer directorId, String sortBy);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getMostPopularFilms(int count, int genreId, int year);

    void deleteFilmById(Integer id);

    List<Film> searchFilms(String query, String by);

}
