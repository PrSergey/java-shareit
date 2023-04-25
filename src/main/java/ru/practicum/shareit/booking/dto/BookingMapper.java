package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingResponseDto toBookingDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

}
