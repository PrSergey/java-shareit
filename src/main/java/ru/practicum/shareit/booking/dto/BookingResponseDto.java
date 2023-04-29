package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.constant.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

    private Long id;

    @NotNull
    private Item item;

    private User booker;
    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    public BookingResponseDto(Long id, Item item, User booker, BookingStatus status) {
        this.id = id;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
