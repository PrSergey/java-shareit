package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();
    }

    @Test
    void toUserDto() {
        User user = createUser();
        UserDto userDto = UserMapper.toUserDto(user);
        assertEquals(user.getId(), userDto.getId());
    }

    @Test
    void fromUserDto() {
        UserDto userDto = UserMapper.toUserDto(createUser());
        User user = UserMapper.fromUserDto(userDto);
        assertEquals(userDto.getId(), user.getId());
    }
}