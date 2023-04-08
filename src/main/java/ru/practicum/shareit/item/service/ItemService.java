package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    ItemDto add(Long userId, ItemDto item);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getUsersItem(Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    List<ItemDto> searchItem(String text);

}
