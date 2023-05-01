package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImp;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    UserServiceImp userService;

    @PostMapping
    public UserDto add(@RequestBody@Valid UserDto user) {
        log.info("Запрос на создание нового пользователя {}", user);
        return userService.add(user);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Запрос на полученеи пользователя с id= {}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                            @RequestBody@Valid UserDto user) {
        log.info("Обновление пользователя с id= {}", userId);
        return userService.update(userId, user);
    }

    @DeleteMapping ("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id= {}", userId);
        userService.deleteUser(userId);
    }


}