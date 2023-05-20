package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.utilShareit.booking.BookingRequestDto;

import static ru.practicum.utilShareit.constant.AuthenticatedUser.authentificatedUser;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    BookingService bookingService;

    @PostMapping
    public BookingResponseDto add(@RequestHeader(authentificatedUser) Long userID,
                                  @RequestBody BookingRequestDto bookingDto) {
        log.info("Запрос на создание нового бронирования {}", bookingDto);
        return bookingService.save(userID, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(authentificatedUser) Long ownerId,
                                              @PathVariable Long bookingId,
                                              @RequestParam (value = "approved", required = false) Boolean approve) {
        log.info("Подтверждение или отклонение {} запроса на бронирование вещи с id= {}", approve, bookingId);
        return bookingService.approveBooking(ownerId, bookingId, approve);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader(authentificatedUser) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос на получение данных о бронирование вещи с id= {} пользователя с id= {}", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByBooker(@RequestHeader(authentificatedUser) Long userId,
                                                       @RequestParam (value = "state", defaultValue = "ALL")
                                                       String bookingState,
                                                       @RequestParam (name = "from", defaultValue = "0") int from,
                                                       @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение списка всех бронирований текущего пользователя с id= {}", userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return bookingService.getBookingByBooker(userId, bookingState, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByOwner(@RequestHeader(authentificatedUser) Long userId,
                                                      @RequestParam (value = "state", defaultValue = "ALL")
                                                      String bookingState,
                                                      @RequestParam (name = "from", defaultValue = "0") int from,
                                                      @RequestParam (name = "size", defaultValue = "10") int size) {
        log.info("Запрос на получение списка бронирований для всех вещей текущего пользователя с id= {}", userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return bookingService.getBookingByOwner(userId, bookingState, pageRequest);
    }


}
