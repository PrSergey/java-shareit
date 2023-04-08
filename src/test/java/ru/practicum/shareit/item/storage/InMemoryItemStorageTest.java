package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImp;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

class InMemoryItemStorageTest {

    ItemServiceImp itemServiceImp;
    InMemoryUserStorage inMemoryUserStorage;
    ItemStorage itemStorage;

    @BeforeEach
    void setUp() {
        itemStorage = new InMemoryItemStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        itemServiceImp = new ItemServiceImp(itemStorage, inMemoryUserStorage);
        inMemoryUserStorage.add(new User(null, "UserTest", "email@user.ru"));
    }

    @Test
    void addItemWithoutAvailable() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemServiceImp.add(1L, new ItemDto("nameItem","descriptionItem", null)));
        Assertions.assertEquals("При добавление вещи, не указан статус доступности", exception.getMessage());
    }

    @Test
    void addItemWithoutName() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemServiceImp.add(1L, new ItemDto(null,"descriptionItem", true)));
        Assertions.assertEquals("При добавление вещи, не указано имя", exception.getMessage());
    }

    @Test
    void addItemWithoutDescription() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemServiceImp.add(1L, new ItemDto("nameItem",null, true)));
        Assertions.assertEquals("При добавление вещи, нет описания", exception.getMessage());
    }

    @Test
    void addItem() {
        itemServiceImp.add(1L, new ItemDto("nameItem","descriptionItem", true));
        Assertions.assertEquals(itemServiceImp.getUsersItem(1L).size(), 1);
    }


    @Test
    void getItemBeforeAdd() {
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> itemServiceImp.getItem(1L, 1L));
        Assertions.assertEquals("Вещи с id=1 не найдено.", exception.getMessage());
    }

    @Test
    void getItem() {
        Assertions.assertEquals(itemServiceImp.getUsersItem(1L).size(), 0);
        itemServiceImp.add(1L, new ItemDto("nameItem","descriptionItem", true));
        itemServiceImp.getItem(1L, 1L);
        Assertions.assertEquals(itemServiceImp.getUsersItem(1L).size(), 1);
        Assertions.assertEquals(itemServiceImp.getItem(1L, 1L).getName(), "nameItem");
    }

    @Test
    void updateItem() {
        itemServiceImp.add(1L, new ItemDto("nameItem","descriptionItem", true));
        Assertions.assertEquals(itemServiceImp.getItem(1L, 1L).getName(), "nameItem");
        itemServiceImp.updateItem(1L,1L,
                new ItemDto("nameNewItem","descriptionItem", true));
        Assertions.assertEquals(itemServiceImp.getItem(1L, 1L).getName(), "nameNewItem");
    }

    @Test
    void searchItem() {
        itemServiceImp.add(1L, new ItemDto("nameItem","descriptionItem", true));
        Assertions.assertEquals(itemServiceImp.searchItem("SearCHitem").size(), 0);
        itemServiceImp.updateItem(1L,1L,
                new ItemDto("searchitem","descriptionItem", true));
        Assertions.assertEquals(itemServiceImp.searchItem("SearCHitem").size(), 1);
    }
}