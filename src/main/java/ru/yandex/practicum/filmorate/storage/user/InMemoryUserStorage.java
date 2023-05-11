package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final AtomicInteger id = new AtomicInteger();
    private final HashSet<User> users = new HashSet<>();

    @Override
    public void addUser(User user) {
        id.getAndIncrement();
        user.setId(id.get());
        users.add(user);
    }

    @Override
    public void updateUser(User user) {
        users.forEach(u -> {
            if (u.getId() == user.getId()) {
                u.setName(user.getName());
                u.setBirthday(user.getBirthday());
                u.setLogin(user.getLogin());
                u.setEmail(user.getEmail());
            }
        });
    }

    @Override
    public void removeUser(User user) {
        users.remove(user);
    }

    public List<User> getUserList() {
        return new ArrayList<>(users);
    }

}
