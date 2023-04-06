package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.user.model.User;


@AllArgsConstructor
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User fromUserDto(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
