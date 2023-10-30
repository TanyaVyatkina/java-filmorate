package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    private DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> findAllDirectors() {
        return directorStorage.findAllDirectors();
    }

    public Director findDirectorById(Integer id) {
        return directorStorage.findDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + id + " не найден."));
    }

    public void removeDirectorById(Integer id) {
        directorStorage.removeDirectorById(id);
    }

    public Director createDirector(Director director) {
        validateDirector(director);
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        checkDirectorExists(director);
        validateDirector(director);
        return directorStorage.updateDirector(director);
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссера должно быть заполнено.");
        }
    }

    private void checkDirectorExists(Director director) {
        directorStorage.findDirectorById(director.getId())
                .orElseThrow(() -> new NotFoundException("Режиссер с id = " + director.getId() + " не найден."));
    }
}
