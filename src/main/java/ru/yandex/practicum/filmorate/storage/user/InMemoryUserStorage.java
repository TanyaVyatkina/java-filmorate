package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int userId = 0;
    private final Map<Integer, User> users = new LinkedHashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getUserId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        Integer id = user.getId();
        users.put(id, user);
        log.debug("Обновлен пользователь c id = : {}", id);
        return user;
    }

    @Override
    public User findById(Integer id) {
        return users.get(id);
    }

    private Integer getUserId() {
        return ++userId;
    }
}
