package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Service
@Slf4j
public class ValidateService {

    public void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.warn("Название фильма не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания не должна превышать 200 символов.");
            throw new ValidationException("Максимальная длина описания не должна превышать 200 символов.");
        }
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза должна быть не раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Электронная почта не может быть пустой.");
            throw new ValidationException("Электронная почта не может быть пустой.");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Электронная почта должна содержать @.");
            throw new ValidationException("Электронная почта должна содержать @.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()
                || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым или содержать пробелы.");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("День Рождения не может быть в будущем.");
            throw new ValidationException("День Рождения не может быть в будущем.");
        }
    }

    public void validateUpdateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Не задан id фильма.");
            throw new IllegalArgumentException("Не задан id фильма.");
        }
        validateFilm(film);
    }

    public void validateUpdateUser(User user) {
        if (user.getId() == null) {
            log.warn("Не задан id пользователя.");
            throw new IllegalArgumentException("Не задан id пользователя.");
        }
        validateUser(user);
    }
}
