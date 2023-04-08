package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto add(UserDto user);

    UserDto update(Long userId, UserDto user);

    UserDto deleteUser(Long userId);
}
