package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Repository
public class InMemoryUserStorage implements UserStorage {

    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public User add(User user) {
        checkDuplicateEmail(user);
        user.setId(id++);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return getById(id - 1L);
    }

    private void checkDuplicateEmail(User user) {
        if (emails.contains(user.getEmail())) {
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
            emails.remove(getById(userId).getEmail());
            users.get(userId).setEmail(user.getEmail());
            emails.add(user.getEmail());
        }
        return getById(userId);
    }

    @Override
    public User deleteUser(Long userId) {
        emails.remove(getById(userId).getEmail());
        return users.remove(userId);
    }

}
