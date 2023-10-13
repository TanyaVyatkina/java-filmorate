package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(UserDbStorage.class)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Autowired
    public UserDbStorageTest(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Test
    void testCreateUser() {
        User actualUser = getUser();
        User resultUser = userStorage.create(actualUser);
        assertThat(resultUser)
                .isNotNull()
                .extracting("id")
                .isNotNull();
        assertUsersEquals(resultUser, actualUser);
    }

    @Test
    void testUpdateUser() {
        User actualUser = userStorage.create(getUser());
        actualUser.setName("New name");
        actualUser.setLogin("New login");
        actualUser.setEmail("newEmail@yandex.ru");
        actualUser.setBirthday(LocalDate.of(2000, 10, 10));
        User resultUser = userStorage.update(actualUser);
        assertThat(resultUser).isNotNull();
        assertUsersEquals(resultUser, actualUser);
    }

    @Test
    void testFindUserById() {
        User actualUser = userStorage.create(getUser());
        Optional<User> result = userStorage.findById(actualUser.getId());
        assertThat(result).isPresent();
        User resultUser = result.get();
        assertThat(resultUser.getId()).isEqualTo(actualUser.getId());
        assertUsersEquals(resultUser, actualUser);
    }

    @Test
    void testFindAllUsers() {
        Integer actualUserId1 = userStorage.create(getUser()).getId();
        Integer actualUserId2 = userStorage.create(getUser()).getId();

        List<User> resultUsers = userStorage.findAll();
        assertThat(resultUsers)
                .isNotNull()
                .size()
                .isEqualTo(2);
        List<Integer> resultIds = resultUsers
                .stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());
        assertThat(resultIds).contains(actualUserId1, actualUserId2);
    }

    @Test
    void testAddFriend() {
        User user1 = userStorage.create(getUser());
        User user2 = userStorage.create(getUser());

        userStorage.addFriend(user1, user2);

        List<User> resultFriends = userStorage.findFriends(user1);
        assertThat(resultFriends)
                .isNotNull()
                .size()
                .isEqualTo(1);
        assertThat(resultFriends.get(0).getId()).isEqualTo(user2.getId());

        userStorage.addFriend(user2, user1);
        resultFriends = userStorage.findFriends(user2);
        assertThat(resultFriends)
                .isNotNull()
                .size()
                .isEqualTo(1);
        assertThat(resultFriends.get(0).getId()).isEqualTo(user1.getId());
    }

    @Test
    void testRemoveFriend() {
        User user1 = userStorage.create(getUser());
        User user2 = userStorage.create(getUser());

        userStorage.addFriend(user1, user2);

        List<User> resultFriends = userStorage.findFriends(user1);
        assertThat(resultFriends)
                .isNotNull()
                .size()
                .isEqualTo(1);
        assertThat(resultFriends.get(0).getId()).isEqualTo(user2.getId());

        userStorage.removeFriend(user1, user2);
        resultFriends = userStorage.findFriends(user2);
        assertThat(resultFriends)
                .size()
                .isEqualTo(0);
    }

    private User getUser() {
        return new User(null, "login@yandex.ru", "login", "name",
                LocalDate.of(1990, 5, 5));
    }

    private void assertUsersEquals(User resultUser, User actualUser) {
        assertThat(resultUser)
                .hasFieldOrPropertyWithValue("id", actualUser.getId())
                .hasFieldOrPropertyWithValue("name", actualUser.getName())
                .hasFieldOrPropertyWithValue("login", actualUser.getLogin())
                .hasFieldOrPropertyWithValue("email", actualUser.getEmail())
                .hasFieldOrPropertyWithValue("birthday", actualUser.getBirthday());
    }
}
