package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private Item createItem() {
        return Item.builder()
                .available(true)
                .description("description")
                .id(1L)
                .name("name")
                .owner(2L)
                .build();
    }

    private Comment createComment() {
        return Comment.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .authorName(new User())
                .item(new Item())
                .text("text comments")
                .build();
    }

    @Test
    void toItemDto() {
        Item item = createItem();
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(itemDto.getId(), item.getId());
    }

    @Test
    void toItemWithBookingDto() {
        Item item = createItem();
        ItemResponseDto itemResponseDto = ItemMapper.toItemWithBookingDto(item,
                new BookingForItemDto(), new BookingForItemDto(), List.of(CommentResponseDto.builder().build()));
        assertEquals(itemResponseDto.getId(), item.getId());
    }

    @Test
    void testToItemWithBookingDto() {
        Item item = createItem();
        ItemResponseDto itemResponseDto = ItemMapper.toItemWithBookingDto(item, List.of(createComment()));

        assertEquals(itemResponseDto.getId(), item.getId());
    }

    @Test
    void fromItemDto() {
        Item item = createItem();
        Item itemAfterDto = ItemMapper.fromItemDto(ItemMapper.toItemDto(item));

        assertEquals(item.getName(), itemAfterDto.getName());
    }
}