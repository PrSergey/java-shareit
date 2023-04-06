package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImp;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryUserStorageTest {

    UserServiceImp userServiceImp;
    InMemoryUserStorage inMemoryUserStorage;

    public User createUser() {
        return new User(null, "UserTest", "email@user.ru");
    }

    @BeforeEach
    public void before() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userServiceImp = new UserServiceImp(inMemoryUserStorage);
    }


    @Test
    void shouldCreateUser() {
        User user = userServiceImp.add(createUser());
        Assertions.assertEquals(user.getName(), "UserTest");
        Assertions.assertEquals(user.getEmail(), "email@user.ru");
    }

    @Test
    void shouldDontCreateUserWithoutEmail() {
        User user = new User(null, "UserTest", null);
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userServiceImp.add(user));
        Assertions.assertEquals("Нет email", exception.getMessage());
    }


    @Test
    void shouldGetById() {
        userServiceImp.add(createUser());
        Assertions.assertEquals(userServiceImp.getById(1L).getEmail(), "email@user.ru");
    }

    @Test
    void shouldExceptionNotUser() {
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> userServiceImp.getById(2L));
        Assertions.assertEquals("Пользвателя с id=2 не найден в базе.", exception.getMessage());
    }

    @Test
    void shouldGetAllUser() {
        Assertions.assertEquals(userServiceImp.getAll().size(), 0);
        userServiceImp.add(createUser());
        Assertions.assertEquals(userServiceImp.getAll().size(), 1);
    }

    @Test
    void shouldUpdateUser() {
        userServiceImp.add(createUser());
        Assertions.assertEquals(userServiceImp.getById(1L).getEmail(), "email@user.ru");
        inMemoryUserStorage.update(1L, new User(null, "UserTest", "emailTest@user.ru"));
        Assertions.assertEquals(userServiceImp.getById(1L).getEmail(), "emailTest@user.ru");
    }

    @Test
    void deleteUser() {
        userServiceImp.add(createUser());
        Assertions.assertEquals(userServiceImp.getAll().size(), 1);
        inMemoryUserStorage.deleteUser(1L);
        Assertions.assertEquals(userServiceImp.getAll().size(), 0);
    }
}