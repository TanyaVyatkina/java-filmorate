package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Include
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;

    public void addFriend(Integer friendId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(friendId);
    }

    public void removeFriend(Integer friendId) {
        friends.remove(friendId);
    }
}
