package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidateServiceTest {

    ValidateService validateService = new ValidateService();

    @Test
    public void validateFilm_EmptyName_ReturnsValidationException() {
        Film film = new Film(1, "", "Description",
                LocalDate.of(2000, 10, 10), 100, 0);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Название фильма не может быть пустым.");
    }

    @Test
    public void validateFilm_WrongDescription_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description".repeat(20),
                LocalDate.of(2000, 10, 10), 100, 0);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Максимальная длина описания не должна превышать 200 символов.");
    }

    @Test
    public void validateFilm_WrongReleaseDate_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1800, 10, 10), 100, 0);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Дата релиза должна быть не раньше 28 декабря 1895 года.");
    }

    @Test
    public void validateFilm_WrongDuration_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 10, 10), -100, 0);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Продолжительность фильма должна быть положительным числом.");
    }

    @Test
    public void validateUser_EmptyEmail_ReturnsValidationException() {
        User user = new User(1, "", "login", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateUser(user));
        assertEquals(result.getMessage(), "Электронная почта не может быть пустой.");
    }

    @Test
    public void validateUser_WrongEmail_ReturnsValidationException() {
        User user = new User(1, "email", "login", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateUser(user));
        assertEquals(result.getMessage(), "Электронная почта должна содержать @.");
    }

    @Test
    public void validateUser_EmptyLogin_ReturnsValidationException() {
        User user = new User(1, "email@com", "", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateUser(user));
        assertEquals(result.getMessage(), "Логин не может быть пустым или содержать пробелы.");
    }

    @Test
    public void validateUser_WrongLogin_ReturnsValidationException() {
        User user = new User(1, "email@com", "Wrong login", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateUser(user));
        assertEquals(result.getMessage(), "Логин не может быть пустым или содержать пробелы.");
    }

    @Test
    public void validateUser_WrongBirthday_ReturnsValidationException() {
        User user = new User(1, "email@com", "login", "name",
                LocalDate.now().plusMonths(1));
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateUser(user));
        assertEquals(result.getMessage(), "День Рождения не может быть в будущем.");
    }
}
