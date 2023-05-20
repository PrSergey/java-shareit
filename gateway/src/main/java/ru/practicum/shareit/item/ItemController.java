package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.utilShareit.exception.ValidationException;
import ru.practicum.utilShareit.item.CommentRequestDto;
import ru.practicum.utilShareit.item.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.utilShareit.constant.AuthenticatedUser.authentificatedUser;


@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(authentificatedUser) Long userId,
                                      @RequestBody ItemDto item) {
        log.info("Запрос на создание новой вещи {}", item);
        if (item.getAvailable() == null) {
            throw new ValidationException("При добавление вещи, не указан статус доступности");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("При добавление вещи, не указано имя");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("При добавление вещи, нет описания");
        }
        return itemClient.add(userId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(authentificatedUser) Long userId,
                                          @PathVariable Long itemId) {
        log.info("Запрос на полученеи item с id= {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItem(@RequestHeader(authentificatedUser) Long userId,
                                              @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение всех вещей пользователя с id= {}", userId);
        return itemClient.getUsersItem(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(authentificatedUser) Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemDto item) {
        log.info("Обновление вещи с id= {}", itemId);
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping ("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(authentificatedUser) Long userId,
                                    @RequestParam(value = "text", required = false) String text,
                                    @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") int from,
                                    @Positive @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на поиск вещей с текстом {}", text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(authentificatedUser) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Запрос на создание комментария {} для вещи с id= {}", commentRequestDto, itemId);
        return itemClient.saveComment(userId, itemId, commentRequestDto);
    }

}
