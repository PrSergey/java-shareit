package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.utilShareit.booking.BookingRequestDto;
import ru.practicum.utilShareit.constant.BookingState;
import ru.practicum.utilShareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.utilShareit.constant.AuthenticatedUser.authentificatedUser;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(authentificatedUser) Long userID,
                                       @RequestBody @Valid BookingRequestDto bookingDto) {
        log.info("Запрос на создание нового бронирования {}", bookingDto);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Некорректно введены данные по началу и окончанию бронирования.");
        }
        return bookingClient.saveBooking(userID, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(authentificatedUser) Long ownerId,
                                              @PathVariable Long bookingId,
                                              @RequestParam (value = "approved", required = false) Boolean approve) {
        log.info("Подтверждение или отклонение {} запроса на бронирование вещи с id= {}", approve, bookingId);
        return bookingClient.approveBooking(ownerId, bookingId, approve);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(authentificatedUser) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос на получение данных о бронирование вещи с id= {} пользователя с id= {}", userId, bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingByBooker(@RequestHeader(authentificatedUser) Long userId,
                                                       @RequestParam (value = "state", defaultValue = "ALL")
                                                       String bookingState,
                                                       @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") int from,
                                                       @Positive @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение списка всех бронирований текущего пользователя с id= {}", userId);
        BookingState state = getState(bookingState);
        return bookingClient.getBookingByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByOwner(@RequestHeader(authentificatedUser) Long userId,
                                                      @RequestParam (value = "state", defaultValue = "ALL")
                                                      String bookingState,
                                                      @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") int from,
                                                      @Positive @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя с id= {}", userId);
        BookingState state = getState(bookingState);
        return bookingClient.getBookingByOwner(userId, state, from, size);
    }

    private BookingState getState(String stateString) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return state;
    }
}
