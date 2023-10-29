package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {
    @EqualsAndHashCode.Include
    private Integer eventId;
    private EventType eventType;
    private EventOperation operation;
    private Integer entityId;
    private Integer userId;
    private Long timestamp;

    public Event(Integer eventId, EventType eventType, EventOperation operation, Integer entityId, Integer userId, Long timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public Event(EventType eventType, EventOperation operation, Integer entityId, Integer userId) {
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }
}
