package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User add(User user);

    User update(Long userId, User user);

    User deleteUser(Long userId);
}
