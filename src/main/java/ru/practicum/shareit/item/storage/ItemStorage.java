package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item add(Long userId, Item item);

    Item getItem(Long userId, Long itemId);

    List<Item> getUsersItem(Long userId);

    Item updateItem (Long userId, Long itemId, Item item);

    List<Item> searchItem (String text);

}
