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
        Optional<User> user = userStorage.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
        return user.get();
    }

    public List<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User updateUser(User user) {
        checkUserExist(user.getId());
        validateService.validateUser(user);

        userStorage.update(user);
        return userStorage.update(user);
    }

    public User createUser(User user) {
        validateService.validateUser(user);

        return userStorage.create(user);
    }

    public void addFriend(Integer id, Integer friendId) {
        checkUserExist(id);
        checkUserExist(friendId);

        userStorage.addFriend(id, friendId);
        userStorage.addFriend(friendId, id);
    }

    public void removeFriend(Integer id, Integer friendId) {
        checkUserExist(id);
        checkUserExist(friendId);

        userStorage.removeFriend(id, friendId);
        userStorage.removeFriend(friendId, id);
    }

    public List<User> getUsersFriends(Integer id) {
        checkUserExist(id);
        return getUsersFromIds(userStorage.getUsersFriends(id));
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        checkUserExist(id);
        checkUserExist(otherId);

        Set<Integer> userFriends = userStorage.getUsersFriends(id);
        Set<Integer> otherUserFriends = userStorage.getUsersFriends(otherId);

        if (userFriends == null || otherUserFriends == null) {
            return Collections.emptyList();
        }
        Set<Integer> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherUserFriends);

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

    private void checkUserExist(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Не задан id пользователя.");
        }
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }
}
