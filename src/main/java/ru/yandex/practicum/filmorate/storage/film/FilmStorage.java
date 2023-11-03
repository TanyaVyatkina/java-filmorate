package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.SearchType;
import ru.yandex.practicum.filmorate.model.enums.SortingType;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(Integer id);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    List<Film> getFilmsByDirectorId(Integer directorId, SortingType sortBy);

    List<Film> getMostPopularFilms(Integer count);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getMostPopularFilmsByYear(Integer count, Integer year);

    List<Film> getMostPopularFilmsByGenre(Integer count, Integer genreId);

    List<Film> getMostPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    void deleteById(Integer id);

    List<Film> searchFilms(String query, List<SearchType> by);

    List<Film> findRecomendedFilms(Integer id);
}
