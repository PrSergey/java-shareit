package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private Item createItem() {
        return Item.builder()
                .available(true)
                .description("description")
                .id(1L)
                .name("name")
                .owner(2L)
                .request(new ItemRequest())
                .build();
    }

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestResponseDto.getId());
    }

    @Test
    void testToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper
                .toItemRequestDto(itemRequest, List.of(ItemMapper.toItemDto(createItem())));
        assertEquals(itemRequest.getId(), itemRequestResponseDto.getId());
    }
}