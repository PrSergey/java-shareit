package ru.practicum.utilShareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @Size(max = 100)
    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    public ItemDto() {
    }

    public ItemDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDto(String name, String description, Boolean available, Long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
