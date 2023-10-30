package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.EventService;
import ru.yandex.practicum.filmorate.service.user.RecommendationsService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private RecommendationsService recommendationsService;
    private EventService eventService;

    @Autowired
    public UserController(UserService userService, RecommendationsService recommendationService, EventService eventService) {
        this.userService = userService;
        this.recommendationsService = recommendationService;
        this.eventService = eventService;
    }

    @Autowired
    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Поиск всех пользователей.");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Integer id) {
        log.debug("Поиск пользователя с id = {}.", id);
        User user = userService.findUserById(id);
        log.debug("Найден пользователь {}.", id);
        return user;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("Пришел запрос на добавление пользователя.");
        User createdUser = userService.createUser(user);
        log.debug("Добавлен пользователь c id = {}", createdUser.getId());
        return createdUser;

    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.debug("Пришел запрос на обновление пользователя.");
        User updatedUser = userService.updateUser(user);
        log.debug("Обновлен пользователь с id = {}", user.getId());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.debug("Пришел запрос на добавление в друзья.");
        userService.addFriend(id, friendId);
        log.debug("Пользователи с id = {} и id = {} добавлены друг другу в друзья.", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.debug("Пришел запрос на удаление из друзей.");
        userService.removeFriend(id, friendId);
        log.debug("Пользователи с id = {} и id = {} удалены друг у друга из друзей.", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUsersFriends(@PathVariable("id") Integer id) {
        log.debug("Поиск всех друзей пользователя id = {}.", id);
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        log.debug("Поиск общих друзей пользователей с id = {} и id = {}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendedFilms(@PathVariable("id") Integer id) {
        log.debug("Поиск рекомендаций для пользователя с id = {}.", id);
        return recommendationsService.getRecommendedFilms(id);
    }

    @DeleteMapping("/{id}")
    public void userDeleteById(@PathVariable("id") final Integer userId) {
        userService.userDeleteById(userId);
        log.debug("Пользователь с id = {} удалён", userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserEvents(@PathVariable("id") Integer id) {
        log.debug("Поиск действий пользователя с id = {}", id);
        return eventService.getEventsByUserId(id);
    }
}
