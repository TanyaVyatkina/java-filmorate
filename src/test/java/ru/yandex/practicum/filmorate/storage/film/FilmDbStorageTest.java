package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class, UserDbStorage.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    public FilmDbStorageTest(FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Test
    void testCreateFilm() {
        Film actualFilm = getFilm();
        Film resultFilm = filmStorage.create(actualFilm);
        assertThat(resultFilm)
                .isNotNull()
                .extracting("id")
                .isNotNull();
        assertFilmsEquals(resultFilm, actualFilm);
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
        assertThat(resultFilm).isNotNull();
        assertFilmsEquals(resultFilm, actualFilm);
    }

    @Test
    void testFindFilmById() {
        Film actualFilm = filmStorage.create(getFilm());
        Optional<Film> result = filmStorage.findById(actualFilm.getId());
        assertThat(result).isPresent();
        Film resultFilm = result.get();
        assertThat(resultFilm.getId()).isEqualTo(actualFilm.getId());
        assertFilmsEquals(resultFilm, actualFilm);
    }

    @Test
    void testFindAllFilms() {
        Integer actualFilmId1 = filmStorage.create(getFilm()).getId();
        Integer actualFilmId2 = filmStorage.create(getFilm()).getId();

        List<Film> resultFilms = filmStorage.findAll();
        assertThat(resultFilms)
                .isNotNull()
                .size()
                .isEqualTo(2);
        List<Integer> resultIds = resultFilms
                .stream()
                .map(film -> film.getId())
                .collect(Collectors.toList());
        assertThat(resultIds).contains(actualFilmId1, actualFilmId2);
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

    private Film getFilm() {
        return new Film(null, "Name", "Description",
                LocalDate.of(2000, 10, 10), 100, 0,
                new Mpa(1, null), new HashSet<>());
    }

    private User getUser() {
        return new User(null, "login@yandex.ru", "login", "name",
                LocalDate.of(1990, 5, 5));
    }

    private void assertFilmsEquals(Film resultFilm, Film actualFilm) {
        assertThat(resultFilm)
                .hasFieldOrPropertyWithValue("id", actualFilm.getId())
                .hasFieldOrPropertyWithValue("name", actualFilm.getName())
                .hasFieldOrPropertyWithValue("description", actualFilm.getDescription())
                .hasFieldOrPropertyWithValue("likesCount", actualFilm.getLikesCount())
                .hasFieldOrPropertyWithValue("duration", actualFilm.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", actualFilm.getReleaseDate());
    }
}
