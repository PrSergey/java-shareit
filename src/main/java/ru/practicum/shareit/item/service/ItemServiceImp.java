package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImp implements ItemService{

    ItemStorage itemStorage;
    UserStorage userStorage;

    @Override
    public Item add(Long userId, Item item) {
        userStorage.getById(userId);
        return itemStorage.add(userId, item);
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        userStorage.getById(userId);
        return itemStorage.getItem(userId, itemId);
    }

    @Override
    public List<Item> getUsersItem(Long userId) {
        userStorage.getById(userId);
        return itemStorage.getUsersItem(userId);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        return itemStorage.updateItem(userId, itemId, item);
    }

    @Override
    public List<Item> searchItem(String text) {
        return itemStorage.searchItem(text);
    }
}
