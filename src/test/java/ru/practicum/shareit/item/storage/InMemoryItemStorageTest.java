package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

class InMemoryItemStorageTest {

    InMemoryItemStorage itemStorage;
    InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach
    void setUp() {
        itemStorage = new InMemoryItemStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryUserStorage.add(new User(null, "UserTest", "email@user.ru"));
    }

    @Test
    void addItemWithoutAvailable() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemStorage.add(1L, new Item("nameItem","descriptionItem", null)));
        Assertions.assertEquals("При добавление вещи, не указан статус доступности", exception.getMessage());
    }

    @Test
    void addItemWithoutName() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemStorage.add(1L, new Item(null,"descriptionItem", true)));
        Assertions.assertEquals("При добавление вещи, не указано имя", exception.getMessage());
    }

    @Test
    void addItemWithoutDescription() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemStorage.add(1L, new Item("nameItem",null, true)));
        Assertions.assertEquals("При добавление вещи, нет описания", exception.getMessage());
    }

    @Test
    void addItem() {
        itemStorage.add(1L, new Item("nameItem","descriptionItem", true));
        Assertions.assertEquals(itemStorage.getUsersItem(1L).size(), 1);
    }


    @Test
    void getItemBeforeAdd() {
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> itemStorage.getItem(1L, 1L));
        Assertions.assertEquals("Вещи с id=1 не найдено.", exception.getMessage());
    }

    @Test
    void getItem() {
        Assertions.assertEquals(itemStorage.getUsersItem(1L).size(), 0);
        itemStorage.add(1L, new Item("nameItem","descriptionItem", true));
        itemStorage.getItem(1L, 1L);
        Assertions.assertEquals(itemStorage.getUsersItem(1L).size(), 1);
        Assertions.assertEquals(itemStorage.getItem(1L, 1L).getName(), "nameItem");
    }

    @Test
    void updateItem() {
        itemStorage.add(1L, new Item("nameItem","descriptionItem", true));
        Assertions.assertEquals(itemStorage.getItem(1L, 1L).getName(), "nameItem");
        itemStorage.updateItem(1L,1L,
                new Item("nameNewItem","descriptionItem", true));
        Assertions.assertEquals(itemStorage.getItem(1L, 1L).getName(), "nameNewItem");
    }

    @Test
    void searchItem() {
        itemStorage.add(1L, new Item("nameItem","descriptionItem", true));
        Assertions.assertEquals(itemStorage.searchItem("SearCHitem").size(), 0);
        itemStorage.updateItem(1L,1L,
                new Item("searchitem","descriptionItem", true));
        Assertions.assertEquals(itemStorage.searchItem("SearCHitem").size(), 1);
    }
}