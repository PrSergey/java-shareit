package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestResponseDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestResponseDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public static ItemRequestResponseDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestResponseDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items);
    }

}
