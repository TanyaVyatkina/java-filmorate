package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
public class RecommendationsService {
    private FilmStorage filmStorage;
    @Autowired
    public RecommendationsService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getRecommendedFilms(Integer id) {
        return filmStorage.findRecomendedFilms(id);
    }
}
