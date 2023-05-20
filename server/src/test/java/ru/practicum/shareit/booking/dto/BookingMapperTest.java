package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .booker(new User(3L, "name", "email@email.com"))
                .item(new Item(4L, 5L, "name", "description", true, new ItemRequest()))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void toBookingForItemDtoTest() {
        Booking booking = createBooking();
        BookingForItemDto bookingForItemDto = BookingMapper.toBookingForItemDto(booking);
        assertEquals(booking.getBooker().getId(), bookingForItemDto.getBookerId());
    }

    @Test
    void toBookingDtoTest() {
        Booking booking = createBooking();
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingDto(booking);
        assertEquals(booking.getId(), bookingResponseDto.getId());
    }

    @Test
    void fromBookingDtoTest() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        Booking booking = BookingMapper.fromBookingDto(bookingRequestDto, new Item(), new User());
        assertEquals(booking.getStart(), bookingRequestDto.getStart());
    }

}