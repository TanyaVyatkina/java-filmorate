package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

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
        return findUserIfExist(id);
    }

    public List<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User updateUser(User user) {
        validateService.validateUpdateUser(user);
        findUserIfExist(user.getId());
        checkAndSetUserName(user);

        return userStorage.update(user);
    }

    public User createUser(User user) {
        validateService.validateUser(user);
        checkAndSetUserName(user);

        return userStorage.create(user);
    }

    public void addFriend(Integer id, Integer friendId) {
        User user = findUserIfExist(id);
        User friend = findUserIfExist(friendId);

        userStorage.addFriend(user, friend);
        userStorage.addFriend(friend, user);
    }

    public void removeFriend(Integer id, Integer friendId) {
        User user = findUserIfExist(id);
        User friend = findUserIfExist(friendId);

        userStorage.removeFriend(user, friend);
        userStorage.removeFriend(friend, user);
    }

    public List<User> getUsersFriends(Integer id) {
        User user = findUserIfExist(id);
        return userStorage.findFriends(user);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        User user = findUserIfExist(id);
        User otherUser = findUserIfExist(otherId);

        List<User> userFriends = userStorage.findFriends(user);
        List<User> otherUserFriends = userStorage.findFriends(otherUser);

        List<User> commonFriends = new ArrayList<>(userFriends);
        commonFriends.retainAll(otherUserFriends);

        return commonFriends;
    }

    private User findUserIfExist(Integer id) {
        return userStorage.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
    }

    private void checkAndSetUserName(User user) {
        if (user.isEmptyName()) {
            user.setName(user.getLogin());
        }
    }
}
