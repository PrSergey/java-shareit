package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Нет email");
        }
        User userAfterAdd = userRepository.save(UserMapper.fromUserDto(user));
        return UserMapper.toUserDto(userAfterAdd);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ExistenceException("Пользвателя с id=" + id + " не найден в базе."));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        User userInMemory = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException("Пользвателя с id=" + userId + " не найден в базе."));
        if (user.getName() != null) {
            userInMemory.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userInMemory.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(userInMemory));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ExistenceException("Пользвателя с id=" + userId + " не найден в базе.");
        }
        userRepository.deleteById(userId);
    }

}