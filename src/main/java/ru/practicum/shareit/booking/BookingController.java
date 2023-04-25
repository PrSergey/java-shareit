package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    BookingService bookingService;

    @PostMapping
    public BookingResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userID,
                                  @RequestBody @Valid BookingRequestDto bookingDto) {
        log.info("Запрос на создание нового бронирования {}", bookingDto);
        return bookingService.save(userID, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @PathVariable Long bookingId,
                                              @RequestParam (value = "approved", required = false) Boolean approve) {
        log.info("Подтверждение или отклонение {} запроса на бронирование вещи с id= {}", approve, bookingId);
        return bookingService.approveBooking(ownerId, bookingId, approve);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос на получение данных о бронирование вещи с id= {} пользователя с id= {}", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam (value = "state", defaultValue = "ALL")
                                                       String bookingState) {
        log.info("Запрос на получение списка всех бронирований текущего пользователя с id= {}", userId);
        return bookingService.getBookingByBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam (value = "state", defaultValue = "ALL")
                                               String bookingState) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя с id= {}", userId);
        return bookingService.getBookingByOwner(userId, bookingState);
    }


}
