package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private Long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Long userId, Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("При добавление вещи, не указан статус доступности");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("При добавление вещи, не указано имя");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("При добавление вещи, нет описания");
        }
        item.setOwner(userId);
        item.setId(id);
        items.put(id++, item);
        return getItem(userId, item.getId());
    }


    @Override
    public Item getItem(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ExistenceException("Вещи с id=" + itemId + " не найдено.");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getUsersItem(Long userId) {
        List<Item> usersItems = new ArrayList<>(items.values());
        return usersItems.stream().filter(i -> i.getOwner() == userId).collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        if (items.get(itemId).getOwner() != userId) {
            throw new ExistenceException("Пользователь с id=" + userId +
                    " не является собствеником вещи с id=" + itemId);
        }
        if (item.getName() != null) {
            items.get(itemId).setName(item.getName());
        }
        if (item.getDescription() != null) {
            items.get(itemId).setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            items.get(itemId).setAvailable(item.getAvailable());
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItem(String text) {
        List<Item> itemWithText = new ArrayList<>();
        String searchText = text.toLowerCase();

        if (text.isBlank()) {
            return itemWithText;
        }
        for (Item item: items.values()) {
            if (item.getAvailable()
                    && item.getDescription().toLowerCase().contains(searchText)
                    || item.getName().toLowerCase().contains(searchText)) {
                itemWithText.add(item);
            }
        }
        return itemWithText;
    }

}
