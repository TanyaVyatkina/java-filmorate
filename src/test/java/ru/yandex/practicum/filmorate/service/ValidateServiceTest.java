package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidateServiceTest {

    ValidateService validateService = new ValidateService();

    @Test
    public void validateFilm_EmptyName_ReturnsValidationException() {
        Film film = new Film(1, "", "Description",
                LocalDate.of(2000, 10, 10), 100);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Название фильма не может быть пустым.");
    }

    @Test
    public void validateFilm_WrongDescription_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description".repeat(20),
                LocalDate.of(2000, 10, 10), 100);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Максимальная длина описания не должна превышать 200 символов.");
    }

    @Test
    public void validateFilm_WrongReleaseDate_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1800, 10, 10), 100);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Дата релиза должна быть не раньше 28 декабря 1895 года.");
    }

    @Test
    public void validateFilm_WrongDuration_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 10, 10), -100);
        ValidationException result = assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
        assertEquals(result.getMessage(), "Продолжительность фильма должна быть положительным числом.");
    }

    @Test
    public void validateUpdateFilm_NullFilmId_ReturnsIllegalArgumentException() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> validateService.validateUpdateFilm(null, new HashMap<>()));
        assertEquals(result.getMessage(), "Не задан id фильма.");
    }

    @Test
    public void validateUpdateFilm_NotSavedFilm_ReturnsIllegalArgumentException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 10, 10), 100);
        HashMap<Integer, Film> films = new LinkedHashMap<>();
        films.put(film.getId(), film);
        NotFoundException result = assertThrows(NotFoundException.class, () -> validateService.validateUpdateFilm(2, films));
        assertEquals(result.getMessage(), "Фильм с id = 2 не найден.");
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

    @Test
    public void validateUpdateUser_NotSavedUser_ReturnsIllegalArgumentException() {
        User user = new User(1, "email@com", "login", "name",
                LocalDate.of(1990, 12, 12));
        Map<Integer, User> users = new LinkedHashMap<>();
        users.put(user.getId(), user);
        NotFoundException result = assertThrows(NotFoundException.class, () -> validateService.validateUpdateUser(2, users));
        assertEquals(result.getMessage(), "Пользователь с id = 2 не найден.");
    }

    @Test
    public void validateUpdateUser_NullUserId_ReturnsIllegalArgumentException() {
        User user = new User(null, "email@com", "login", "name",
                LocalDate.of(1990, 12, 12));
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> validateService.validateUpdateUser(null, new HashMap<>()));
        assertEquals(result.getMessage(), "Не задан id пользователя.");
    }
}
