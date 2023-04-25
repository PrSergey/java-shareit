package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemResponseDto toItemWithBookingDto(Item item,
                                                       BookingForItemDto lastBooking,
                                                       BookingForItemDto nextBooking,
                                                       List<CommentResponseDto> comments) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }


    public static ItemResponseDto toItemWithBookingDto(Item item,
                                                       List<CommentResponseDto> comments) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                comments
        );
    }

    public static Item fromItemDto(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

}
