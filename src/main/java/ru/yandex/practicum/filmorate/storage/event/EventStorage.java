package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    List<Event> findByUserId(Integer userId);

    Event save(Event event);
}
