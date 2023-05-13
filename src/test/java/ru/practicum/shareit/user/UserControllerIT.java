package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    private final String path = "/users";

    @SneakyThrows
    @Test
    void add_whenUsersEmailIsNotValid_thenBadRequestThrow() {
        UserDto userForSave = new UserDto();
        userForSave.setEmail("email.ru");

        mvc.perform(post(path)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userForSave)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).add(userForSave);
    }

    @SneakyThrows
    @Test
    void add_whenUserIsValid_thenReturnUserDto() {
        UserDto userForSave = new UserDto();
        userForSave.setEmail("emai@email.ru");
        when(userService.add(userForSave))
                .thenReturn(userForSave);

        String userAfterSave = mvc.perform(post(path)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userForSave)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).add(userForSave);
        assertEquals(userAfterSave, objectMapper.writeValueAsString(userForSave));
    }

    @SneakyThrows
    @Test
    void getById() {
        Long userId = 1L;
        UserDto userForSave = new UserDto();
        userForSave.setEmail("emai@email.ru");
        when(userService.getById(userId))
                .thenReturn(userForSave);

        String getUser = mvc.perform(get(path + "/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getById(userId);
        assertEquals(getUser, objectMapper.writeValueAsString(userForSave));
    }

    @SneakyThrows
    @Test
    void getAll() {
        Long userId = 1L;
        UserDto userForSave = new UserDto();
        userForSave.setEmail("emai@email.ru");
        List<UserDto> listUsers = List.of(userForSave);
        when(userService.getAll())
                .thenReturn(listUsers);

        String getUser = mvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getAll();
        assertEquals(getUser, objectMapper.writeValueAsString(listUsers));
    }

    @SneakyThrows
    @Test
    void update_whenUserIsNotValid_thenNotUpdateAndBadRequestThrow() {
        long userId = 2L;
        UserDto userForSave = new UserDto();
        userForSave.setEmail("email.ru");

        mvc.perform(patch(path + "/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userForSave)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(userId, userForSave);
    }

    @SneakyThrows
    @Test
    void update() {
        long userId = 2L;
        UserDto userForSave = new UserDto();
        userForSave.setEmail("email@email.ru");
        when(userService.update(userId, userForSave))
                .thenReturn(userForSave);

        String userAfterUpdate = mvc.perform(patch(path + "/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userForSave)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).update(userId, userForSave);
        assertEquals(userAfterUpdate, objectMapper.writeValueAsString(userForSave));
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 2L;
        UserDto userForSave = new UserDto();
        userForSave.setEmail("email@email.ru");

        mvc.perform(delete(path + "/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userForSave)))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}