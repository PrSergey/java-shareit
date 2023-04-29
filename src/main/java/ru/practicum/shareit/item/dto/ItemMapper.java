package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithComments;

import java.util.stream.Collectors;


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

    public static ItemResponseDto toItemWithBookingDto(ItemWithComments item,
                                                       BookingForItemDto lastBooking,
                                                       BookingForItemDto nextBooking) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                item.getComments()
                        .stream()
                        .map(CommentsMapper::toCommentResponseDto).collect(Collectors.toList())
        );
    }


    public static ItemResponseDto toItemWithBookingDto(ItemWithComments item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getComments()
                        .stream()
                        .map(CommentsMapper::toCommentResponseDto).collect(Collectors.toList())
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
