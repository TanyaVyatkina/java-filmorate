package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAllRatings() {
        return mpaStorage.findAllRatings();
    }

    public Mpa findRatingById(Integer id) {
        return mpaStorage.findRatingById(id).orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден."));
    }
}
