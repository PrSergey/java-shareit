package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemService {

    Item add(Long userId, Item item);

    Item getItem(Long userId, Long itemId);

    List<Item> getUsersItem(Long userId);

    Item updateItem(Long userId, Long itemId, Item item);

    List<Item> searchItem(String text);

}
