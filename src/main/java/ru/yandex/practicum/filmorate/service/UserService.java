package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.Status;
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
        user1.getFriends().add(new Friends(userId, userFriendId, Status.AWAITING_CONFIRM));
        user2.getFriends().add(new Friends(userFriendId, userId, Status.AWAITING_CONFIRM));
        return user1;
    }

    public User removeForFriends(long userId, long userFriendId) {
        removeFriends(userId, userFriendId);
        removeFriends(userFriendId, userId);
        return getUser(userFriendId);
    }

    public List<User> listFriends(long userId) {
        List<User> friendList = new ArrayList<>();
        getUser(userId)
                .getFriends()
                .stream()
                .filter(f -> f.getStatus().equals(Status.FRIEND))
                .forEach(i -> friendList.add(getUser(i.getFriendTwo())));
        return friendList;
    }

    public List<User> listGeneralFriends(long userid, long userFriend) {
        List<User> friendList = new ArrayList<>();
        getUser(userid)
                .getFriends()
                .stream()
                .filter(f -> f.getStatus().equals(Status.FRIEND))
                .map(Friends::getFriendTwo)
                .flatMap(f -> getUser(userFriend)
                        .getFriends()
                        .stream()
                        .filter(fr -> fr.getStatus().equals(Status.FRIEND))
                        .map(Friends::getFriendTwo)
                        .filter(s -> Objects.equals(s, f)))
                .forEach(x -> friendList.add(getUser(x)));
        return friendList;
    }

    private void removeFriends(long user, long friend) {
        getUser(user).getFriends()
                .remove(getUser(user).getFriends()
                        .stream()
                        .filter(f -> f.getFriendTwo() == friend)
                        .findFirst()
                        .orElseThrow(() -> new UserNotFoundException(friend)));
    }

    private boolean checkContainUsers(User user) {
        return inMemoryUserStorage.getUserList().stream().anyMatch(u -> u.getId() == user.getId());
    }

    public User getUser(long id) {
        return inMemoryUserStorage.getUserList().stream().filter(f -> f.getId() == id).findFirst()
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
