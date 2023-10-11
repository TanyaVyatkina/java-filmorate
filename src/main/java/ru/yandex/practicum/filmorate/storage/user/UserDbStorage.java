package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("dbUserStorage")
public class UserDbStorage implements UserStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int id = simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update users set " +
                "email = :email, login = :login, name = :name, birthday = :birthday " +
                "where user_id = :user_id";
        jdbcTemplate.update(sqlQuery, toMap(user));
        return user;
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sqlQuery = "select * from users where user_id = :user_id";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, Collections.singletonMap("user_id", id));
        if (userRows.next()) {
            User user = new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()
            );
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "insert into friendship (user_id, friend_id) " + "values (:user_id, :friend_id)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("friend_id", friend.getId());
        jdbcTemplate.update(sqlQuery, parameters);
    }

    @Override
    public void removeFriend(User user, User friend) {
        String sqlQuery = "delete from friendship where user_id = :user_id and friend_id = :friend_id";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("friend_id", friend.getId());
        jdbcTemplate.update(sqlQuery, parameters);
    }

    @Override
    public List<User> findFriends(User user) {
        String sql = "select * from users where user_id in (select friend_id from friendship where user_id = :user_id)";

        SqlParameterSource namedParameters = new MapSqlParameterSource("user_id", user.getId());
        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
        return user;
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", user.getName());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());
        values.put("email", user.getEmail());
        if (user.getId() != null) {
            values.put("user_id", user.getId());
        }
        return values;
    }
}
