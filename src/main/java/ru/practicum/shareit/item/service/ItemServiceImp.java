package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ItemServiceImp implements ItemService {

    ItemStorage itemStorage;
    UserStorage userStorage;

    @Override
    public ItemDto add(Long userId, ItemDto item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("При добавление вещи, не указан статус доступности");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("При добавление вещи, не указано имя");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("При добавление вещи, нет описания");
        }
        userStorage.getById(userId);
        Item itemAfterAdd = itemStorage.add(userId, ItemMapper.fromItemDto(item));
        return ItemMapper.toItemDto(itemAfterAdd);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        userStorage.getById(userId);
        return ItemMapper.toItemDto(itemStorage.getItem(userId, itemId));
    }

    @Override
    public List<ItemDto> getUsersItem(Long userId) {
        userStorage.getById(userId);
        List<ItemDto> items = new ArrayList<>();
        itemStorage.getUsersItem(userId).forEach(i -> items.add(ItemMapper.toItemDto(i)));
        return items;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        Item itemInMemory = itemStorage.getItem(userId, itemId);
        if (!Objects.equals(itemInMemory.getOwner(), userId)) {
            throw new ExistenceException("Пользователь с id=" + userId +
                    " не является собствеником вещи с id=" + itemId);
        }
        if (item.getName() != null) {
            itemInMemory.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemInMemory.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemInMemory.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, itemInMemory));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> items = new ArrayList<>();
        itemStorage.searchItem(text).forEach(i -> items.add(ItemMapper.toItemDto(i)));
        return items;
    }

}
