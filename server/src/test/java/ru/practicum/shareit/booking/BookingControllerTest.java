package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final String PATH = "/bookings";

    @SneakyThrows
    @Test
    void request_whenAutorizationNotProvided_thenBadRequest() {
        mvc.perform(get(PATH)).andExpect(status().isBadRequest());
        mvc.perform(post(PATH)).andExpect(status().isBadRequest());
        mvc.perform(patch(PATH)).andExpect(status().isMethodNotAllowed());
    }

    @SneakyThrows
    @Test
    void add_whenBookingDtoValid_thenSave() {
        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(1L);
        bookingToSave.setStart(LocalDateTime.now().plusMinutes(1L));
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(2L));
        long userId = 0L;

        mvc.perform(post(PATH)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingToSave))
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).save(userId, bookingToSave);
    }

    @SneakyThrows
    @Test
    void approveBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        String approve = "true";

        mvc.perform(patch(PATH + "/{bookingId}", bookingId)
                .param("approved", approve)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).approveBooking(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        long bookingId = 0L;
        long userId = 0L;

        mvc.perform(get(PATH + "/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService).getBookingById(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void getBookingByBooker_whenBookingsIsValid_thenValidationException() {
        Long userId = 1L;
        String state = "ALL";
        String from = "1";
        String size = "2";
        BookingResponseDto bookingInMemory = new BookingResponseDto();
        List<BookingResponseDto> bookings = List.of(bookingInMemory);
        PageRequest pageRequest = PageRequest
                .of(Integer.parseInt(from) / Integer.parseInt(size), Integer.parseInt(size));
        when(bookingService.getBookingByBooker(userId, state, pageRequest))
                .thenReturn(bookings);

        String contentAsString = mvc.perform(get(PATH)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        verify(bookingService).getBookingByBooker(userId, state, pageRequest);
        assertEquals(objectMapper.writeValueAsString(bookings), contentAsString);

    }

    @SneakyThrows
    @Test
    void getBookingByOwner() {
        Long userId = 1L;
        String state = "ALL";
        String from = "1";
        String size = "2";
        BookingResponseDto bookingInMemory = new BookingResponseDto();
        List<BookingResponseDto> bookings = List.of(bookingInMemory);
        PageRequest pageRequest = PageRequest
                .of(Integer.parseInt(from) / Integer.parseInt(size), Integer.parseInt(size));
        when(bookingService.getBookingByOwner(userId, state, pageRequest))
                .thenReturn(bookings);

        String contentAsString = mvc.perform(get(PATH + "/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        verify(bookingService).getBookingByOwner(userId, state, pageRequest);
        assertEquals(objectMapper.writeValueAsString(bookings), contentAsString);

    }
}