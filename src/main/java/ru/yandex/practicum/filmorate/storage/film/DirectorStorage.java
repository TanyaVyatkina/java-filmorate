package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> findAllDirectors();

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Optional<Director> findDirectorById(Integer id);

    List<Director> findDirectorsByIdList(List<Integer> ids);

    void removeDirectorById(Integer id);
}
