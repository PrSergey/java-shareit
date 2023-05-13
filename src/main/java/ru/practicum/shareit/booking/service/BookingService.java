package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto save(Long userID, BookingRequestDto bookingDto);

    BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approve);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingByBooker(Long bookerId, String state, PageRequest pageRequest);

    List<BookingResponseDto> getBookingByOwner(Long ownerId, String state, PageRequest pageRequest);

}
