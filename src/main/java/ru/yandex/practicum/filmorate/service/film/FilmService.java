package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.service.user.EventService;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    private MpaStorage mpaStorage;
    private GenreStorage genreStorage;

    private DirectorStorage directorStorage;
    private ValidateService validateService;
    private EventService eventService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage,
                       MpaStorage mpaStorage, DirectorStorage directorStorage, ValidateService validateService, EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validateService = validateService;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
        this.eventService = eventService;
    }

    public Film findFilmById(Integer id) {
        return findFilmIfExist(id);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film updateFilm(Film film) {
        validateService.validateUpdateFilm(film);
        checkRatingExists(film);
        checkGenresExists(film);
        checkDirectorsExists(film);
        findFilmIfExist(film.getId());

        return filmStorage.update(film);
    }

    public Film createFilm(Film film) {
        validateService.validateFilm(film);
        checkRatingExists(film);
        checkGenresExists(film);
        checkDirectorsExists(film);

        return filmStorage.create(film);
    }

    public void addLike(Integer id, Integer userId) {
        Film film = findFilmIfExist(id);
        User user = findUserIfExist(userId);

        filmStorage.addLike(film, user);
        eventService.addEvent(new Event(EventType.LIKE, EventOperation.ADD, id, userId));
    }

    public void removeLike(Integer id, Integer userId) {
        Film film = findFilmIfExist(id);
        User user = findUserIfExist(userId);

        filmStorage.removeLike(film, user);
        eventService.addEvent(new Event(EventType.LIKE, EventOperation.REMOVE, id, userId));
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public List<Film> getMostPopularFilmsByYear(Integer count, Integer year) {
        return filmStorage.getMostPopularFilmsByYear(count, year);
    }

    public List<Film> getMostPopularFilmsByGenre(Integer count, Integer genreId) {
        return filmStorage.getMostPopularFilmsByGenre(count, genreId);
    }

    public List<Film> getMostPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    public List<Film> getFilmsByDirectorId(Integer directorId, String sortBy) {
        directorStorage.findDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + directorId + " не найден."));
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    private Film findFilmIfExist(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден."));
    }

    private User findUserIfExist(Integer id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private void checkRatingExists(Film film) {
        Mpa mpa = film.getMpa();
        if (mpa == null) return;
        mpaStorage.findRatingById(mpa.getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + mpa.getId() + " не найден."));
    }

    private void checkGenresExists(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) return;
        List<Integer> genreIds = genres
                .stream()
                .map(g -> g.getId())
                .collect(Collectors.toList());
        List<Genre> resultGenres = genreStorage.findGenresByIdList(genreIds);
        List<Integer> wrongGenres = genres
                .stream()
                .filter(genre -> !resultGenres.contains(genre))
                .map(genre -> genre.getId())
                .collect(Collectors.toList());
        if (!wrongGenres.isEmpty()) {
            throw new NotFoundException("Не найдены жанры с id = "
                    + wrongGenres);
        }
    }

    private void checkDirectorsExists(Film film) {
        Set<Director> directors = film.getDirectors();
        if (directors == null || directors.isEmpty()) return;
        List<Integer> directorIds = directors
                .stream()
                .map(d -> d.getId())
                .collect(Collectors.toList());
        List<Director> resultDirectors = directorStorage.findDirectorsByIdList(directorIds);
        List<Integer> wrongIds = directors
                .stream()
                .filter(dir -> !resultDirectors.contains(dir))
                .map(dir -> dir.getId())
                .collect(Collectors.toList());
        if (!wrongIds.isEmpty()) {
            throw new NotFoundException("Не найдены режиссеры с id = " + wrongIds);
        }
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);
        return commonFilms;
    }

    public void deleteById(int filmId) {
        filmStorage.deleteById(filmId);
    }

    public List<Film> getRecommendedFilms(Integer id) {
        return filmStorage.findRecomendedFilms(id);
    }
}
