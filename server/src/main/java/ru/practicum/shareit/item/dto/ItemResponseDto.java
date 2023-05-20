package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long request;

    private BookingForItemDto lastBooking;

    private BookingForItemDto nextBooking;

    private List<CommentResponseDto> comments;

    public ItemResponseDto() {
    }

    public ItemResponseDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ItemResponseDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemResponseDto(Long id, String name, String description, Boolean available, Long request, List<CommentResponseDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
        this.comments = comments;
    }
}
