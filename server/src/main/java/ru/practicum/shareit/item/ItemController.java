package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    ItemService itemService;
    private final String authentificatedUser = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(authentificatedUser) Long userId,
                        @RequestBody ItemDto item) {
        log.info("Запрос на создание новой вещи {}", item);
        return itemService.add(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(authentificatedUser) Long userId,
                           @PathVariable Long itemId) {
        log.info("Запрос на полученеи item с id= {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getUsersItem(@RequestHeader(authentificatedUser) Long userId,
                                              @RequestParam (name = "from", defaultValue = "0") int from,
                                              @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение всех вещей пользователя с id= {}", userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemService.getUsersItem(userId, pageRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(authentificatedUser) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto item) {
        log.info("Обновление вещи с id= {}", itemId);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping ("/search")
    public List<ItemDto> searchItem(@RequestHeader(authentificatedUser) Long userId,
                                    @RequestParam(value = "text", required = false) String text,
                                    @RequestParam (name = "from", defaultValue = "0") int from,
                                    @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на поиск вещей с текстом {}", text);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemService.searchItem(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(authentificatedUser) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Запрос на создание комментария {} для вещи с id= {}", commentRequestDto, itemId);
        return itemService.saveComment(itemId, userId, commentRequestDto);
    }

}
