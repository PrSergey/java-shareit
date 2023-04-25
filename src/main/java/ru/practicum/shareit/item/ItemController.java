package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
                        @RequestBody ItemDto item) {
        log.info("Запрос на создание новой вещи {}", item);
        return itemService.add(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Запрос на полученеи item с id= {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getUsersItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех вещей пользователя с id= {}", userId);
        return itemService.getUsersItem(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto item) {
        log.info("Обновление вещи с id= {}", itemId);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping ("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "text", required = false) String text) {
        log.info("Запрос на поиск вещей с текстом {}", text);
        return itemService.searchItem(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Запрос на создание комментария {} для вещи с id= {}", commentRequestDto, itemId);
        return itemService.saveComment(itemId,userId, commentRequestDto);
    }

}
