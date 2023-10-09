package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("memoryUserStorage")
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
    public List<User> findFriends(User user) {
        return getUsersFromIds(usersFriends.get(user.getId()));
    }

    private Integer getUserId() {
        return ++userId;
    }

    private List<User> getUsersFromIds(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return users.values()
                .stream()
                .filter(u -> ids.contains(u.getId()))
                .collect(Collectors.toList());
    }
}
