package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    FilmController filmController = new FilmController();

    @Test
    public void create_EmptyName_ReturnsValidationException() {
        Film film = new Film(1, "", "Description",
                LocalDate.of(2000, 10, 10), 100);
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.create(film);
            }
        });
        assertEquals(result.getMessage(), "Название фильма не может быть пустым.");
    }

    @Test
    public void create_WrongDescription_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description".repeat(20),
                LocalDate.of(2000, 10, 10), 100);
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.create(film);
            }
        });
        assertEquals(result.getMessage(), "Максимальная длина описания не должна превышать 200 символов.");
    }

    @Test
    public void create_WrongReleaseDate_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1800, 10, 10), 100);
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.create(film);
            }
        });
        assertEquals(result.getMessage(), "Дата релиза должна быть не раньше 28 декабря 1895 года.");
    }

    @Test
    public void create_WrongDuration_ReturnsValidationException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 10, 10), -100);
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.create(film);
            }
        });
        assertEquals(result.getMessage(), "Продолжительность фильма должна быть положительным числом.");
    }

    @Test
    public void update_NullFilm_ReturnsIllegalArgumentException() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.update(null);
            }
        });
        assertEquals(result.getMessage(), "Фильм не задан.");
    }

    @Test
    public void create_NotSavedFilm_ReturnsIllegalArgumentException() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 10, 10), 100);
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                filmController.update(film);
            }
        });
        assertEquals(result.getMessage(), "Фильм с id = 1 не найден.");
    }
}
