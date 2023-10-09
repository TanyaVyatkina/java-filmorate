package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateUser() {
        User resultUser = userStorage.create(getUser());
        assertThat(resultUser)
                .isNotNull()
                .extracting("id")
                .isNotNull();
    }

    @Test
    void testUpdateUser() {
        User actualUser = userStorage.create(getUser());
        actualUser.setName("New name");
        actualUser.setLogin("New login");
        actualUser.setEmail("newEmail@yandex.ru");
        actualUser.setBirthday(LocalDate.of(2000, 10, 10));
        User resultUser = userStorage.update(actualUser);
        assertThat(resultUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", actualUser.getId())
                .hasFieldOrPropertyWithValue("name", actualUser.getName())
                .hasFieldOrPropertyWithValue("login", actualUser.getLogin())
                .hasFieldOrPropertyWithValue("email", actualUser.getEmail())
                .hasFieldOrPropertyWithValue("birthday", actualUser.getBirthday());
    }

    @Test
    void testFindUserById() {
        User actualUser = userStorage.create(getUser());
        Optional<User> resultUser = userStorage.findById(actualUser.getId());
        assertThat(resultUser)
                .isPresent()
                .get()
                .isEqualTo(actualUser);
    }

    @Test
    void testFindAllUsers() {
        User actualUser1 = userStorage.create(getUser());
        User actualUser2 = userStorage.create(getUser());

        List<User> resultUsers = userStorage.findAll();
        assertThat(resultUsers)
                .isNotNull()
                .contains(actualUser1, actualUser2)
                .size()
                .isEqualTo(2);
    }

    @Test
    void testAddFriend() {
        User user1 = userStorage.create(getUser());
        User user2 = userStorage.create(getUser());

        userStorage.addFriend(user1, user2);

        List<User> resultFriends = userStorage.findFriends(user1);
        assertThat(resultFriends)
                .isNotNull()
                .contains(user2)
                .size()
                .isEqualTo(1);

        userStorage.addFriend(user2, user1);
        resultFriends = userStorage.findFriends(user2);
        assertThat(resultFriends)
                .isNotNull()
                .contains(user1)
                .size()
                .isEqualTo(1);
    }

    @Test
    void testRemoveFriend() {
        User user1 = userStorage.create(getUser());
        User user2 = userStorage.create(getUser());

        userStorage.addFriend(user1, user2);

        List<User> resultFriends = userStorage.findFriends(user1);
        assertThat(resultFriends)
                .isNotNull()
                .contains(user2)
                .size()
                .isEqualTo(1);

        userStorage.removeFriend(user1, user2);
        resultFriends = userStorage.findFriends(user2);
        assertThat(resultFriends)
                .size()
                .isEqualTo(0);
    }

    @Test
    void testFindGenreById() {
        Optional<Genre> resultGenre = filmStorage.findGenreById(1);
        assertThat(resultGenre)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void testFindAllGenres() {
        List<Genre> resultGenres = filmStorage.findAllGenres();
        assertThat(resultGenres)
                .isNotNull()
                .size()
                .isEqualTo(6);
        List<String> resultGenreNames = resultGenres
                .stream()
                .map(genre -> genre.getName())
                .collect(Collectors.toList());
        assertThat(resultGenreNames).containsExactlyInAnyOrder("Комедия", "Триллер", "Боевик", "Мультфильм",
                "Драма", "Документальный");
    }

    @Test
    void testFindRatingById() {
        Optional<Mpa> resultRating = filmStorage.findRatingById(1);
        assertThat(resultRating)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    void testFindAllRatings() {
        List<Mpa> resultRatings = filmStorage.findAllRatings();
        assertThat(resultRatings)
                .isNotNull()
                .size()
                .isEqualTo(5);
        List<String> resultRatingNames = resultRatings
                .stream()
                .map(mpa -> mpa.getName())
                .collect(Collectors.toList());
        assertThat(resultRatingNames).containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void testCreateFilm() {
        Film resultFilm = filmStorage.create(getFilm());
        assertThat(resultFilm)
                .isNotNull()
                .extracting("id")
                .isNotNull();
    }

    @Test
    void testUpdateFilm() {
        Film actualFilm = filmStorage.create(getFilm());
        actualFilm.setName("New name");
        actualFilm.setDescription("New description");
        actualFilm.setDuration(200);
        actualFilm.setReleaseDate(LocalDate.of(2020, 10, 10));
        actualFilm.setLikesCount(15);
        Film resultFilm = filmStorage.update(actualFilm);
        assertThat(resultFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", actualFilm.getId())
                .hasFieldOrPropertyWithValue("name", actualFilm.getName())
                .hasFieldOrPropertyWithValue("description", actualFilm.getDescription())
                .hasFieldOrPropertyWithValue("likesCount", actualFilm.getLikesCount())
                .hasFieldOrPropertyWithValue("duration", actualFilm.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", actualFilm.getReleaseDate());
    }

    @Test
    void testFindFilmById() {
        Film actualFilm = filmStorage.create(getFilm());
        Optional<Film> resultFilm = filmStorage.findById(actualFilm.getId());
        assertThat(resultFilm)
                .isPresent()
                .get()
                .isEqualTo(actualFilm);
    }

    @Test
    void testFindAllFilms() {
        Film actualFilm1 = filmStorage.create(getFilm());
        Film actualFilm2 = filmStorage.create(getFilm());

        List<Film> resultFilms = filmStorage.findAll();
        assertThat(resultFilms)
                .isNotNull()
                .contains(actualFilm1, actualFilm2)
                .size()
                .isEqualTo(2);
    }

    @Test
    void testAddLike() {
        User user = userStorage.create(getUser());
        Film film = filmStorage.create(getFilm());

        filmStorage.addLike(film, user);

        Set<Integer> resultLikes = filmStorage.getLikesByFilmId(film.getId());
        assertThat(resultLikes)
                .isNotNull()
                .contains(user.getId())
                .size()
                .isEqualTo(1);
    }

    @Test
    void testRemoveLike() {
        User user1 = userStorage.create(getUser());
        User user2 = userStorage.create(getUser());

        Film film = filmStorage.create(getFilm());

        filmStorage.addLike(film, user1);
        filmStorage.addLike(film, user2);

        Set<Integer> resultLikes = filmStorage.getLikesByFilmId(film.getId());
        assertThat(resultLikes)
                .isNotNull()
                .contains(user1.getId(), user2.getId())
                .size()
                .isEqualTo(2);

        filmStorage.removeLike(film, user1);

        resultLikes = filmStorage.getLikesByFilmId(film.getId());
        assertThat(resultLikes)
                .isNotNull()
                .contains(user2.getId())
                .size()
                .isEqualTo(1);
    }

    private User getUser() {
        return new User(null, "login@yandex.ru", "login", "name",
                LocalDate.of(1990, 5, 5));
    }

    Film getFilm() {
        return new Film(null, "Name", "Description",
                LocalDate.of(2000, 10, 10), 100, 0,
                new Mpa(1, null), new HashSet<>());
    }

}
