package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(GenreDbStorage.class)
public class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreDbStorageTest(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Test
    void testFindGenreById() {
        Optional<Genre> resultGenre = genreStorage.findGenreById(1);
        assertThat(resultGenre)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void testFindAllGenres() {
        List<Genre> resultGenres = genreStorage.findAllGenres();
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
}
