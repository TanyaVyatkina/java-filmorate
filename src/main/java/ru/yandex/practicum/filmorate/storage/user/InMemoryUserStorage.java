package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int userId = 0;
    private Map<Integer, Set<Integer>> usersFriends = new HashMap<>();
    private final Map<Integer, User> users = new LinkedHashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(getUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        Integer id = user.getId();
        users.put(id, user);
        return user;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(User user, User friend) {
        Integer userId = user.getId();
        if (usersFriends.get(userId) == null) {
            usersFriends.put(userId, new HashSet<>());
        }
        usersFriends.get(userId).add(friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        usersFriends.get(user.getId()).remove(friend.getId());
    }

    @Override
    public Set<Integer> findFriends(User user) {
        return usersFriends.get(user.getId());
    }

    private Integer getUserId() {
        return ++userId;
    }
}
