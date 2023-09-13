package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;
    private ValidateService validateService;

    @Autowired
    public UserService(UserStorage userStorage, ValidateService validateService) {
        this.userStorage = userStorage;
        this.validateService = validateService;
    }

    public User findUserById(Integer id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
        return user;
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User updateUser(User user) {
        validateService.validateUserExist(user.getId());
        validateService.validateUser(user);

        return userStorage.update(user);
    }

    public User createUser(User user) {
        validateService.validateUser(user);

        return userStorage.create(user);
    }

    public void addFriend(Integer id, Integer friendId) {
        User user = validateService.validateUserExist(id);
        User friend = validateService.validateUserExist(friendId);

        user.addFriend(friendId);
        friend.addFriend(id);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Integer id, Integer friendId) {
        User user = validateService.validateUserExist(id);
        User friend = validateService.validateUserExist(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(id);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<User> getUsersFriends(Integer id) {
        User user = validateService.validateUserExist(id);
        return getUsersFromIds(user.getFriends());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        User user = validateService.validateUserExist(id);
        User otherUser = validateService.validateUserExist(otherId);
        if (user.getFriends() == null || otherUser.getFriends() == null) {
            return Collections.emptyList();
        }
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return getUsersFromIds(commonFriends);
    }

    private List<User> getUsersFromIds(Set<Integer> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userStorage.findAll()
                .stream()
                .filter(u -> ids.contains(u.getId()))
                .collect(Collectors.toList());
    }
}
