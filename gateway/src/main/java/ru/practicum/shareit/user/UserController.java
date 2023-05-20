package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Validated
public class UserController {


    UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody@Valid UserDto user) {
        log.info("Запрос на создание нового пользователя {}", user);
        if (user.getEmail() == null) {
            throw new ValidationException("Нет email");
        }
        return userClient.saveUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("Запрос на полученеи пользователя с id= {}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                            @RequestBody@Valid UserDto user) {
        log.info("Обновление пользователя с id= {}", userId);
        return userClient.update(userId, user);
    }

    @DeleteMapping ("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id= {}", userId);
        userClient.delete(userId);
    }
}