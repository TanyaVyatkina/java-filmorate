package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Include
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public boolean isEmptyName() {
        return name == null || name.isBlank();
    }
}
