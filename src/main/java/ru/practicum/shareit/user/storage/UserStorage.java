package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User getById(Long id);

    List<User> getAll();

    User update(Long userId, User user);

    void deleteUser(Long userId);

}
