package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.utilShareit.request.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.utilShareit.constant.AuthenticatedUser.authentificatedUser;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemRequestController {

    ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(authentificatedUser) Long userId,
            @RequestBody @Valid ItemRequestDto itemRequest) {
        log.info("Post-запрос на создание запроса на вещь");
        return itemRequestClient.save(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getPersonalRequests(
            @RequestHeader(authentificatedUser) Long userId) {
        log.info("Get-запрос на получение всех своих запросов на вещь");
        return itemRequestClient.getPersonalRequests(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(authentificatedUser) Long userId,
            @PathVariable Long requestId) {
        log.info("Get-запрос на получение запроса на вещь");
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(
            @RequestHeader(authentificatedUser) Long userId,
            @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") int from,
            @Positive @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Get-запрос на получение всех запросов на вещи");
        return itemRequestClient.getItemRequests(userId, from, size);
    }


}
