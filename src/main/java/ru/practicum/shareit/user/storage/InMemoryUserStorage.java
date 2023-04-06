package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class InMemoryUserStorage implements UserStorage {

    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        checkDuplicateEmail(user);
        if (user.getEmail() == null) {
            throw new ValidationException("Нет email");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return getById(id - 1L);
    }

    private void checkDuplicateEmail(User user) {
        List<User> checkEmail = users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());

        if (users.size() > 0 && checkEmail.size() > 0) {
            throw new EmailException("Пользователь с email=" + user.getEmail() + " уже существует.");
        }
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new ExistenceException("Пользвателя с id=" + id + " не найден в базе.");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(Long userId, User user) {
        if (user.getEmail() != null && !user.getEmail().equals(users.get(userId).getEmail())) {
            checkDuplicateEmail(user);
        }
        if (user.getName() != null) {
            users.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {
            users.get(userId).setEmail(user.getEmail());
        }
        return getById(userId);
    }

    @Override
    public User deleteUser(Long userId) {
        getById(userId);
        return users.remove(userId);
    }

}
