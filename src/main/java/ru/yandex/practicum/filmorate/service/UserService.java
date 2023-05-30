package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    public void addFriends(long id, long friendId) {
        userDbStorage.UserById(id);
        userDbStorage.UserById(friendId);
        String sqlQuery = """
                insert into  users_friend(user_id, friend_id)                 
                values (?, ?)""";
        jdbcTemplate.update(sqlQuery,
                id, friendId
        );


    }

    public void deleteFriends(Long id, Long friendId) {
        userDbStorage.UserById(id);
        userDbStorage.UserById(friendId);
        String sqlQuery = """
                delete from users_friend 
                where user_id = ? 
                and friend_id = ?
                """;
        jdbcTemplate.update(sqlQuery, id, friendId);

    }

    public List<User> getCommonFriends(Long id, Long friendId) {
        Set<Long> friendsList = getSetIdFriends(id);
        Set<Long> secondFriendsList = getSetIdFriends(friendId);
        if (friendsList.isEmpty() || secondFriendsList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return secondFriendsList.stream()
                    .filter(friendsList::contains)
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }

    }

    public List<User> getSetFriends(Long userId) {
        Set<Long> listId = getSetIdFriends(userId);
        return listId.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());

    }

    public User getUserById(Long userId) {
        return userDbStorage.UserById(userId);
    }

    public User create(User user) {
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        userDbStorage.UserById(user.getId());
        userDbStorage.updateUser(user);
        return user;
    }

    public Set<Long> getSetIdFriends(Long userId) {
        userDbStorage.UserById(userId);
        Set<Long> listIdUser = new HashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                """
                        select * 
                        from users_friend 
                        where user_id = ?
                        """, userId);
        while (rs.next()) {
            listIdUser.add((long) rs.getInt("friend_id"));
        }
        return listIdUser;
    }
}
