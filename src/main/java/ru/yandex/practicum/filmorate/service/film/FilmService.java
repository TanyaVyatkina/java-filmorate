package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.ValidateService;
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

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage,
                       MpaStorage mpaStorage, DirectorStorage directorStorage, ValidateService validateService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validateService = validateService;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
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
        checkDirectorsExists(film.getDirectors());
        findFilmIfExist(film.getId());

        return filmStorage.update(film);
    }

    public Film createFilm(Film film) {
        validateService.validateFilm(film);
        checkRatingExists(film.getMpa());
        checkGenresExists(film.getGenres());
        checkDirectorsExists(film.getDirectors());

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

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> f2.getLikesCount() - f1.getLikesCount())
                .limit(count)
                .collect(Collectors.toList());
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

    private void checkDirectorsExists(Set<Director> directors) {
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
}
