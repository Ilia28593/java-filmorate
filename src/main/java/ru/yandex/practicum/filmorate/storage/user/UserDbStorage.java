package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final AtomicInteger id = new AtomicInteger();

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("""
                select * 
                from users""");
        if (rs.next()) {
            User newUser = new User();
            newUser.setEmail(rs.getString("email").trim());
            newUser.setLogin(rs.getString("login").trim());
            newUser.setName(rs.getString("name").trim());
            newUser.setBirthday(rs.getDate("birthday").toLocalDate());
            newUser.setId(rs.getInt("id"));
            users.add(newUser);
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        id.getAndIncrement();
        user.setId(Long.parseLong(String.valueOf(id)));
        String sqlQuery = """
                insert into users(id, email, login, name, birthday)
                values (?, ?, ?, ?, ?)""";
        jdbcTemplate.update(sqlQuery, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = """
                update users set email = ?, login = ?, name = ?, birthday = ? 
                where id = ?""";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return UserById(user.getId());
    }


    @Override
    public User UserById(Long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("""
                select * from users 
                where id = ?""", id);
        if (rs.next()) {
            User newUser = new User(
                    rs.getString("email").trim(),
                    rs.getString("login").trim(),
                    rs.getString("name").trim(),
                    rs.getDate("birthday").toLocalDate()
            );
            newUser.setId(rs.getInt("id"));
            return newUser;
        } else {
            throw new UserNotFoundException(id);
        }
    }
}
