package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void create_EmptyEmail_ReturnsValidationException() {
        User user = new User(1, "", "login", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.create(user);
            }
        });
        assertEquals(result.getMessage(), "Электронная почта не может быть пустой.");
    }

    @Test
    public void create_WrongEmail_ReturnsValidationException() {
        User user = new User(1, "email", "login", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.create(user);
            }
        });
        assertEquals(result.getMessage(), "Электронная почта должна содержать @.");
    }

    @Test
    public void create_EmptyLogin_ReturnsValidationException() {
        User user = new User(1, "email@com", "", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.create(user);
            }
        });
        assertEquals(result.getMessage(), "Логин не может быть пустым или содержать пробелы.");
    }

    @Test
    public void create_WrongLogin_ReturnsValidationException() {
        User user = new User(1, "email@com", "Wrong login", "name",
                LocalDate.of(1990, 12, 12));
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.create(user);
            }
        });
        assertEquals(result.getMessage(), "Логин не может быть пустым или содержать пробелы.");
    }

    @Test
    public void create_WrongBirthday_ReturnsValidationException() {
        User user = new User(1, "email@com", "login", "name",
                LocalDate.now().plusMonths(1));
        ValidationException result = assertThrows(ValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.create(user);
            }
        });
        assertEquals(result.getMessage(), "День Рождения не может быть в будущем.");
    }

    @Test
    public void create_NotSavedUser_ReturnsIllegalArgumentException() {
        User user = new User(1, "email@com", "login", "name",
                LocalDate.of(1990, 12, 12));
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.update(user);
            }
        });
        assertEquals(result.getMessage(), "Пользователь с id = 1 не найден.");
    }

    @Test
    public void update_NullUser_ReturnsIllegalArgumentException() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userController.update(null);
            }
        });
        assertEquals(result.getMessage(), "Пользователь не задан.");
    }
}
