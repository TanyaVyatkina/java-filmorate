package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int filmId = 0;
    private Map<Integer, Set<Integer>> likes = new HashMap<>();
    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Integer id = film.getId();
        films.put(id, film);
        return film;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    public void addLike(Film film, User user) {
        int oldLikesCount = film.getLikesCount();
        film.setLikesCount(++oldLikesCount);
        Integer filmId = film.getId();
        if (likes.get(filmId) == null) {
            likes.put(filmId, new HashSet<>());
        }
        likes.get(filmId).add(user.getId());
    }

    public void removeLike(Film film, User user) {
        int oldLikesCount = film.getLikesCount();
        film.setLikesCount(--oldLikesCount);
        likes.get(film.getId()).remove(user.getId());
    }

    private int getFilmId() {
        return ++filmId;
    }
}
