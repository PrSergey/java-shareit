package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.utilShareit.constant.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.utilShareit.booking.BookingForItemDto;
import ru.practicum.utilShareit.booking.BookingRequestDto;

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

    public static Booking fromBookingDto(BookingRequestDto booking, Item item, User user) {
        return new Booking(
                item,
                user,
                booking.getStart(),
                booking.getEnd(),
                BookingStatus.WAITING
        );
    }



    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

}
