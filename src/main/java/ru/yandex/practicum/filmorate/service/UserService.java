package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserStorage {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Override
    public void addUser(User user) {
        if (!checkContainUsers(user)) {
            inMemoryUserStorage.addUser(user);
        } else {
            throw new ObjectAlreadyExistsException(String.format("This user %s is content in repository.", user.getName()));
        }
    }

    @Override
    public void updateUser(User user) {
        if (checkContainUsers(user)) {
            inMemoryUserStorage.updateUser(user);
        } else {
            throw new UserNotFoundException(user.getId());
        }
    }

    @Override
    public void removeUser(User user) {
        if (checkContainUsers(user)) {
            inMemoryUserStorage.removeUser(user);
        } else {
            throw new UserNotFoundException(user.getId());
        }
    }

    public List<User> getUserList() {
        return inMemoryUserStorage.getUserList();
    }

    public User addForFriends(long userId, long userFriendId) {
        User user1 = getUser(userId);
        User user2 = getUser(userFriendId);
        user1.getSetFriendsId().add(userFriendId);
        user2.getSetFriendsId().add(userId);
        return user1;
    }

    public User removeForFriends(long userId, long userFriendId) {
        getUser(userId)
                .getSetFriendsId().remove(userFriendId);
        getUser(userFriendId)
                .getSetFriendsId().remove(userId);
        return getUser(userFriendId);
    }

    public List<User> listFriends(long userId) {
        List<User> friendList = new ArrayList<>();
        getUser(userId)
                .getSetFriendsId()
                .forEach(i -> friendList.add(getUser(i)));
        return friendList;
    }

    public List<User> listGeneralFriends(long userid, long userFriend) {
        List<User> frienList = new ArrayList<>();
        getUser(userid)
                .getSetFriendsId().stream()
                .flatMap(f -> getUser(userFriend)
                        .getSetFriendsId().stream()
                        .filter(s -> Objects.equals(s, f)))
                .forEach(x -> frienList.add(getUser(x)));
        return frienList;
    }

    private boolean checkContainUsers(User user) {
        return inMemoryUserStorage.getUserList().stream().anyMatch(u -> u.getId() == user.getId());
    }

    public User getUser(long id) {
        return inMemoryUserStorage.getUserList().stream().filter(f -> f.getId() == id).findFirst()
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
