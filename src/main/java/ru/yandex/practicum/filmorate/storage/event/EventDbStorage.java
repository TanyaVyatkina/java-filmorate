package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventDbStorage implements EventStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EventDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> findEventsByUserId(Integer userId) {
        String sql = "select * from events where user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> makeEvent(rs));
    }

    @Override
    public Event addEvent(Event event) {
        String sqlQuery = "insert into events (event_type, operation, entity_id, user_id, timestamp) " +
                "values (:eventType, :operation, :entityId, :userId, :timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sqlQuery, new MapSqlParameterSource().addValues(toMap(event)),
                keyHolder, new String[]{"event_id"});

        int id = keyHolder.getKey().intValue();
        event.setEventId(id);
        return event;
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Event event = new Event(
                rs.getInt("event_id"),
                EventType.valueOf(rs.getString("event_type")),
                EventOperation.valueOf(rs.getString("operation")),
                rs.getInt("entity_id"),
                rs.getInt("user_id"),
                rs.getLong("timestamp")
        );

        return event;
    }

    private Map<String, Object> toMap(Event event) {
        Map<String, Object> values = new HashMap<>();
        if (event.getEventId() != null) {
            values.put("eventId", event.getEventId());
        }

        values.put("eventType", event.getEventType().name());
        values.put("operation", event.getOperation().name());
        values.put("entityId", event.getEntityId());
        values.put("userId", event.getUserId());
        values.put("timestamp", event.getTimestamp());

        return values;
    }
}
