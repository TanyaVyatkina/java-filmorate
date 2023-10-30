package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> findById(Integer id);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> findFriends(User user);

    List<Integer> getUsersFilms(Integer userId);

    List<User> findCrossLikesUsers(Integer id);

    void deleteUserById(int id);
}
