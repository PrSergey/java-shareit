package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto add(UserDto user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Нет email");
        }
        User userAfterAdd = userStorage.add(UserMapper.fromUserDto(user));
        return UserMapper.toUserDto(userAfterAdd);
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userStorage.getById(id));
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        userStorage.getAll().forEach(i -> users.add(UserMapper.toUserDto(i)));
        return users;
    }


    @Override
    public UserDto update(Long userId, UserDto user) {
        return UserMapper.toUserDto(userStorage.update(userId, UserMapper.fromUserDto(user)));
    }

    @Override
    public UserDto deleteUser(Long userId) {
        return UserMapper.toUserDto(userStorage.deleteUser(userId));
    }

}