package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationsService {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public RecommendationsService(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    public Set<Film> getRecommendedFilms(Integer userId) {
        Map<Integer, List<Integer>> filmsOfUsers = new HashMap<>();

        //получение списка пользователей
        List<User> users = userService.getCrossLikesUsers(userId);

        //получение всех фильмов лайкнутых пользователем
        for (User user : users) {
            filmsOfUsers.put(user.getId(), userService.getUsersFilms(user.getId()));
        }

        long maxMatches = 0;
        Set<Integer> similarity = new HashSet<>(); //хранит id пользователей с наибольшими совпадениями по лайкам
        for (Integer id : filmsOfUsers.keySet()) {
            if (id == userId) continue;

            long numberOfMatches = filmsOfUsers.get(id).stream()
                    .filter(filmId -> filmsOfUsers.get(userId).contains(filmId)).count();

            if (numberOfMatches == maxMatches & numberOfMatches != 0) {
                similarity.add(id);
            }

            if (numberOfMatches > maxMatches) {
                maxMatches = numberOfMatches;
                similarity = new HashSet<>();
                similarity.add(id);
            }
        }

        if (maxMatches == 0) return new HashSet<>();
        else return similarity.stream().flatMap(idUser -> userService.getUsersFilms(idUser).stream())
                .filter(filmId -> !filmsOfUsers.get(userId).contains(filmId))
                .map(filmId -> filmService.findFilmById(filmId))
                .collect(Collectors.toSet());
    }
}
