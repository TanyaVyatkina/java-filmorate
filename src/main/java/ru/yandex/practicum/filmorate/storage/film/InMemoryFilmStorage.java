package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int filmId = 0;

    private final Map<Integer, Film> films = new LinkedHashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getFilmId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        Integer id = film.getId();
        films.put(id, film);
        log.debug("Обновлен фильм с id = : {}", id);
        return film;
    }

    @Override
    public Film findById(Integer id) {
        return films.get(id);
    }

    private int getFilmId() {
        return ++filmId;
    }
}
