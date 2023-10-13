package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(MpaDbStorage.class)
public class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Autowired
    public MpaDbStorageTest(MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Test
    void testFindRatingById() {
        Optional<Mpa> resultRating = mpaStorage.findRatingById(1);
        assertThat(resultRating)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    void testFindAllRatings() {
        List<Mpa> resultRatings = mpaStorage.findAllRatings();
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
}
