package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private Long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Long userId, Item item) {
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
        return items.values()
                .stream()
                .filter(i -> Objects.equals(i.getOwner(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        return items.put(itemId, item);
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
