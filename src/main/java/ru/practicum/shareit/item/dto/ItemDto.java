package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @Size(max=100)
    private String name;

    private String description;

    private Boolean available;

    private Long request;
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
}
