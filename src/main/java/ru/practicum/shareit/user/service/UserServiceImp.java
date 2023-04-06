package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class UserServiceImp {

    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(Long userId, User user) {
        return userStorage.update(userId, user);
    }

    public User deleteUser(Long userId){
        return userStorage.deleteUser(userId);
    }

}