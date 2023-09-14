package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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

    public void addLike(Integer id, Integer userId) {
        Film film = films.get(id);
        int oldLikesCount = film.getLikesCount();
        film.setLikesCount(++oldLikesCount);
        if (likes.get(id) == null) {
            likes.put(id, new HashSet<>());
        }
        likes.get(id).add(userId);
    }

    public void removeLike(Integer id, Integer userId) {
        Film film = films.get(id);
        int oldLikesCount = film.getLikesCount();
        film.setLikesCount(--oldLikesCount);
        likes.get(id).remove(userId);
    }

    private int getFilmId() {
        return ++filmId;
    }
}
