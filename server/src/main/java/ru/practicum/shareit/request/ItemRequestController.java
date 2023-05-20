package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {

    private final String authentificatedUser = "X-Sharer-User-Id";

    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto add(
            @RequestHeader(authentificatedUser) Long userId,
            @RequestBody ItemRequestDto itemRequest) {
        log.info("Post-запрос на создание запроса на вещь");
        return itemRequestService.save(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getPersonalRequests(
            @RequestHeader(authentificatedUser) Long userId) {
        log.info("Get-запрос на получение всех своих запросов на вещь");
        return itemRequestService.getPersonalRequests(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestResponseDto getItemRequestById(
            @RequestHeader(authentificatedUser) Long userId,
            @PathVariable Long requestId) {
        log.info("Get-запрос на получение всех своих запросов на вещь");
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getItemRequests(
            @RequestHeader(authentificatedUser) Long userId,
            @RequestParam (name = "from", defaultValue = "0") int from,
            @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Get-запрос на получение всех запросов на вещь");
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRequestService.getItemRequests(userId, pageRequest);
    }


}
