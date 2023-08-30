package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 0;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateUser(user);
        user.setId(getUserId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user.getEmail());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validateUser(user);
        int id = user.getId();
        if (users.get(id) == null) {
            log.warn("Пользователь с id = " + id + " не найден");
            throw new IllegalArgumentException("Пользователь с id = " + id + " не найден.");
        }
        users.put(id, user);
        log.debug("Обновлен пользователь c id = : {}", user.getId());
        return user;
    }

    private void validateUser(User user) {
        if (user == null) {
            log.warn("Пользователь не задан.");
            throw new IllegalArgumentException("Пользователь не задан.");
        }
        String error = null;
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            error = "Электронная почта не может быть пустой.";
        } else if (!user.getEmail().contains("@")) {
            error = "Электронная почта должна содержать @.";
        } else if (user.getLogin() == null || user.getLogin().isEmpty()
                || user.getLogin().contains(" ")) {
            error = "Логин не может быть пустым или содержать пробелы.";
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            error = "День Рождения не может быть в будущем.";
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (error != null) {
            log.warn(error);
            throw new ValidationException(error);
        }
    }

    private int getUserId() {
        return ++userId;
    }
}
