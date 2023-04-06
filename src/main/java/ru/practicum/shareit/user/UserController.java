package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImp;

import javax.validation.Valid;
import java.util.ArrayList;
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
        User userAfterAdd = userService.add(UserMapper.fromUserDto(user));
        return UserMapper.toUserDto(userAfterAdd);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Запрос на полученеи пользователя с id= {}", userId);
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        List<UserDto> users = new ArrayList<>();
        userService.getAll().forEach(i -> users.add(UserMapper.toUserDto(i)));
        return users;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                            @RequestBody@Valid UserDto user) {
        log.info("Обновление пользователя с id= {}", userId);
        return UserMapper.toUserDto(userService.update(userId, UserMapper.fromUserDto(user)));
    }

    @DeleteMapping ("/{userId}")
    public UserDto deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id= {}", userId);
        return UserMapper.toUserDto(userService.deleteUser(userId));
    }


}