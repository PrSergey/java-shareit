package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody ItemDto item){
        log.info("Запрос на создание новой вещи {}", item);
        Item itemAfterAdd = itemService.add(userId, ItemMapper.fromItemDto(item));
        return ItemMapper.
                toItemDto(itemAfterAdd);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId){
        log.info("Запрос на полученеи item с id= {}", itemId);
        return ItemMapper.toItemDto(itemService.getItem(userId, itemId));
    }

    @GetMapping
    public List<ItemDto> getUsersItem (@RequestHeader("X-Sharer-User-Id") Long userId){
        log.info("Запрос на получение всех вещей пользователя с id= {}", userId);
        List<ItemDto> items = new ArrayList<>();
        itemService.getUsersItem(userId).forEach(i -> items.add(ItemMapper.toItemDto(i)));
        return items;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto item){
        log.info("Обновление вещи с id= {}", itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, ItemMapper.fromItemDto(item)));
    }

    @GetMapping ("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "text", required = false) String text){
        log.info("Запрос на поиск вещей с текстом {}", text);
        List<ItemDto> items = new ArrayList<>();
        itemService.searchItem(text).forEach(i -> items.add(ItemMapper.toItemDto(i)));
        return items;
    }

}
