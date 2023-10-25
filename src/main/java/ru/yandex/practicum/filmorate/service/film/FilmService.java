package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
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
    private ValidateService validateService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage,
                       MpaStorage mpaStorage, ValidateService validateService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validateService = validateService;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film findFilmById(Integer id) {
        return findFilmIfExist(id);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film updateFilm(Film film) {
        validateService.validateUpdateFilm(film);
        checkRatingExists(film.getMpa());
        checkGenresExists(film.getGenres());
        findFilmIfExist(film.getId());

        return filmStorage.update(film);
    }

    public Film createFilm(Film film) {
        validateService.validateFilm(film);
        checkRatingExists(film.getMpa());
        checkGenresExists(film.getGenres());

        return filmStorage.create(film);
    }

    public void addLike(Integer id, Integer userId) {
        Film film = findFilmIfExist(id);
        User user = findUserIfExist(userId);

        filmStorage.addLike(film, user);
    }

    public void removeLike(Integer id, Integer userId) {
        Film film = findFilmIfExist(id);
        User user = findUserIfExist(userId);

        filmStorage.removeLike(film, user);
    }

    public List<Film> getMostPopularFilms(int count, int genreId, int year) {
        if (genreId == 0 && year == 0) {
            return filmStorage.findAll().stream()
                    .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                    .limit(count)
                    .collect(Collectors.toList());
        } else if (genreId == 0 && year != 0) {
            //селект по всем жанрам и по конкретному году
            return filmStorage.findAllByYear(year).stream()
                    .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                    .limit(count)
                    .collect(Collectors.toList());
        } else if (genreId != 0 && year == 0) {
            //селект по конкретному жанру и по всем годам
            return filmStorage.findAllByGenre(genreId).stream()
                    .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            //селект по конкретному жанру и по конкрутному году
            return filmStorage.findAllByGenreAndYear(genreId, year).stream()
                    .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    private Film findFilmIfExist(Integer id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден."));
    }

    private User findUserIfExist(Integer id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private void checkRatingExists(Mpa mpa) {
        if (mpa == null) return;
        mpaStorage.findRatingById(mpa.getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + mpa.getId() + " не найден."));
    }

    private void checkGenresExists(Set<Genre> genres) {
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
}
