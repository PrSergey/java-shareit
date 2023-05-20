package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    UserServiceImp userServiceImp;

    @Captor
    ArgumentCaptor<User> argumentCaptor;

    private UserDto createUserDto() {
        return new UserDto(1L, "name", "email@email.com");
    }

    @Test
    void add_whenEmailIsNull_thenValidationExceptionThrow() {
        UserDto userDto = createUserDto();
        userDto.setEmail(null);

        ValidationException ex = assertThrows(ValidationException.class, () -> userServiceImp.add(userDto));
        assertEquals(ex.getMessage(),"Нет email");
    }

    @Test
    void add_whenUserIsValid_thenReturnUserDto() {
        UserDto userDto = createUserDto();
        when(userRepository.save(any()))
                .thenReturn(UserMapper.fromUserDto(userDto));

        UserDto userAfterAdd = userServiceImp.add(userDto);
        assertEquals(userAfterAdd, userDto);
        verify(userRepository).save(argumentCaptor.capture());
        User userForSave = argumentCaptor.getValue();
        assertEquals(userForSave.getEmail(), userDto.getEmail());
    }

    @Test
    void getById_whenUserNotFound_thenExistenceExceptionThrow() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ExistenceException ex = assertThrows(ExistenceException.class, () -> userServiceImp.getById(userId));
        assertEquals(ex.getMessage(),"Пользвателя с id=" + userId + " не найден в базе.");
    }

    @Test
    void getById_whenUserFound_thenReturnUSerDto() {
        long userId = 1L;
        UserDto userDto = createUserDto();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));

        UserDto getUser = userServiceImp.getById(userId);

        assertEquals(getUser, userDto);
    }

    @Test
    void getAll_whenUserNotFound_thenReturnEmptyList() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        List<UserDto> all = userServiceImp.getAll();

        assertEquals(all.size(), 0);
    }

    @Test
    void getAll_whenUserFoundUser_thenReturnListWithUser() {
        UserDto userDto = createUserDto();
        when(userRepository.findAll())
                .thenReturn(List.of(UserMapper.fromUserDto(userDto)));

        List<UserDto> all = userServiceImp.getAll();

        assertEquals(all.size(), 1);
        assertEquals(all.get(0), userDto);
    }

    @Test
    void update_whenUserNotFoundInMemory_thenExistenceExceptionThrow() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> userServiceImp.update(userId, createUserDto()));
        assertEquals(ex.getMessage(), "Пользвателя с id=" + userId + " не найден в базе.");
    }

    @Test
    void update_whenUserFoundAndUpdate_thenReturnUserDtoAfterUpdate() {
        long userId = 1L;
        UserDto userDto = createUserDto();
        userDto.setId(userId);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
        String newEmail = "newEmai@emal.com";
        String newName = "new name";
        UserDto newUserDto = new UserDto(99L, newName, newEmail);
        when(userRepository.save(any()))
                .thenReturn(UserMapper.fromUserDto(newUserDto));

        userServiceImp.update(userId, newUserDto);

        verify(userRepository).save(argumentCaptor.capture());
        User userForSave = argumentCaptor.getValue();
        assertEquals(userDto.getId(), userForSave.getId());
        assertEquals(userForSave.getName(), newName);
        assertEquals(userForSave.getEmail(), newEmail);
    }

    @Test
    void deleteUser_whenUSerNotFound_thenExistenceExceptionThrow() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(false);

        ExistenceException ex = assertThrows(ExistenceException.class, () -> userServiceImp.deleteUser(userId));
        assertEquals(ex.getMessage(), "Пользвателя с id=" + userId + " не найден в базе.");
    }
}