package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ItemRequestResponseDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;

    public ItemRequestResponseDto(Long id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }

    public ItemRequestResponseDto(Long id, String description, LocalDateTime created, List<ItemDto> items) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.items = items;
    }

    public ItemRequestResponseDto() {
    }
}
