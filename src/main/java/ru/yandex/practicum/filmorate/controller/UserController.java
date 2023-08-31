package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateService;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int userId = 0;
    private ValidateService validateService = new ValidateService();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateService.validateUser(user);
        user.setId(getUserId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user.getEmail());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validateService.validateUser(user);
        Integer id = user.getId();
        validateService.validateUpdateUser(id, users);
        users.put(id, user);
        log.debug("Обновлен пользователь c id = : {}", user.getId());
        return user;
    }

    private Integer getUserId() {
        return ++userId;
    }
}
