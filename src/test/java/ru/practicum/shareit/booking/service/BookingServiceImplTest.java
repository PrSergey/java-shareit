package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constant.BookingStatus;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Captor
    ArgumentCaptor<Booking> argumentCaptor;

    @InjectMocks
    BookingServiceImpl bookingService;



    private User createUser() {
        return User.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .available(true)
                .description("description")
                .id(1L)
                .name("name")
                .owner(2L)
                .build();
    }

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
    void save_whenTimeEndIsNotValid_thenValidationExceptionTrow() {
        long itemId = 1L;
        BookingRequestDto bookingWithFailTime = new BookingRequestDto();
        bookingWithFailTime.setStart(LocalDateTime.now().plusMinutes(1));
        bookingWithFailTime.setEnd(LocalDateTime.now().minusDays(1));
        bookingWithFailTime.setItemId(itemId);

        assertThrows(ValidationException.class, () -> bookingService.save(1L, bookingWithFailTime));
    }

    @Test
    void save_whenTimeStartIsEqualEnd_thenValidationExceptionTrow() {
        Item item = createItem();
        BookingRequestDto bookingWithFailTime = new BookingRequestDto();
        bookingWithFailTime.setStart(LocalDateTime.now().plusMinutes(2));
        bookingWithFailTime.setEnd(bookingWithFailTime.getStart());
        bookingWithFailTime.setItemId(item.getId());

        assertThrows(ValidationException.class, () -> bookingService.save(2L, bookingWithFailTime));
    }


    @Test
    void save_whenTimeStartIsBeforeEnd_thenExistenceExceptionTrow() {
        BookingRequestDto bookingWithFailTime = new BookingRequestDto();
        bookingWithFailTime.setStart(LocalDateTime.now().plusMinutes(2));
        bookingWithFailTime.setEnd(LocalDateTime.now().plusMinutes(3));
        bookingWithFailTime.setItemId(1L);

        assertThrows(ExistenceException.class, () -> bookingService.save(1L, bookingWithFailTime));
    }

    @Test
    void save_whenNotHaveItem_thenValidationExceptionTrow() {
        Item item = createItem();
        BookingRequestDto bookingWithFailTime = new BookingRequestDto();
        bookingWithFailTime.setStart(LocalDateTime.now().plusMinutes(2));
        bookingWithFailTime.setEnd(LocalDateTime.now().plusMinutes(1));
        bookingWithFailTime.setItemId(item.getId());

        assertThrows(ValidationException.class, () -> bookingService.save(2L, bookingWithFailTime));
    }

    @Test
    void save_whenNotHaveUser_thenExistenceExceptionTrow() {
        Item item = createItem();
        BookingRequestDto bookingWithFailTime = new BookingRequestDto();
        bookingWithFailTime.setStart(LocalDateTime.now().plusMinutes(2));
        bookingWithFailTime.setEnd(LocalDateTime.now().plusMinutes(3));
        bookingWithFailTime.setItemId(item.getId());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ExistenceException.class, () -> bookingService.save(1L, bookingWithFailTime));
    }

    @Test
    void save_whenAvailableFalse_thenValidationExceptionTrow() {
        Item item = createItem();
        User user = createUser();
        item.setAvailable(false);
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(2));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(3));
        bookingDto.setItemId(item.getId());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> bookingService.save(1L, bookingDto));
    }

    @Test
    void save_whenBookerIsEqualOwner_thenExistenceExceptionTrow() {
        Item item = createItem();
        User user = createUser();
        item.setOwner(1L);
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(2));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(3));
        bookingDto.setItemId(item.getId());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(ExistenceException.class, () -> bookingService.save(1L, bookingDto));
    }

    @Test
    void save_whenBookingIsValid_thenReturnBookingResponseDto() {
        Item item = createItem();
        User user = createUser();
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setStart(LocalDateTime.now().plusMinutes(2));
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(3));
        bookingDto.setItemId(item.getId());
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Booking bookingAfterSave = new Booking(item, user, bookingDto.getStart(), bookingDto.getEnd(),
                BookingStatus.WAITING);
        bookingAfterSave.setId(1L);
        when(bookingRepository.save(any()))
                .thenReturn(bookingAfterSave);

        bookingService.save(user.getId(), bookingDto);

        verify(bookingRepository).save(argumentCaptor.capture());
        Booking bookingForSave = argumentCaptor.getValue();
        bookingForSave.setId(1L);
        assertEquals(BookingMapper.toBookingDto(bookingForSave), BookingMapper.toBookingDto(bookingAfterSave));
        assertEquals(bookingForSave.getItem(), item);
        assertEquals(bookingForSave.getBooker(), user);
    }


    @Test
    void approveBooking_whenNotFoundBooking_thenExistenceExceptionThrow() {
        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> bookingService.approveBooking(1L, 1L, true));
        assertEquals(ex.getMessage(), "Бронирование с id=" + 1L + " не найдено в базе.");
    }

    @Test
    void approveBooking_whenBookerAndOwnerIsEqual_thenExistenceExceptionThrow() {
        Booking booking = createBooking();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        Long ownerId = 3L;

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> bookingService.approveBooking(ownerId, 1L, true));

        assertEquals(ex.getMessage(), "Пользователь с id=" + ownerId +
                " не может поменять статус, так как он арендатор.");
    }

    @Test
    void approveBooking_whenItemsOwnerAndOwnerIsEqual_thenValidationExceptionThrow() {
        Booking booking = createBooking();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        Long ownerId = 1L;

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(ownerId, booking.getItem().getOwner(), true));

        assertEquals(ex.getMessage(), "Пользователь с id=" + ownerId +
                " не является собствеником вещи с id=" + booking.getItem().getId());
    }

    @Test
    void approveBooking_whenStatusEqualApprove_thenValidationExceptionThrow() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        Long ownerId = 5L;

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(ownerId, booking.getId(), true));

        assertEquals(ex.getMessage(), "У бронирования с id=" + booking.getId() +
                " уже подтвержден статус");
    }

    @Test
    void approveBooking_whenApproveTrue_thenReturnBookingResponseDtoWithBookingStatusAPPROVED() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));
        Long ownerId = 5L;
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingResponseDto actualBooking = bookingService.approveBooking(ownerId, booking.getId(), true);

        assertEquals(actualBooking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_whenApproveFalse_thenReturnBookingResponseDtoWithBookingStatusRejected() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));
        Long ownerId = 5L;
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingResponseDto actualBooking = bookingService.approveBooking(ownerId, booking.getId(), false);

        assertEquals(actualBooking.getStatus(), BookingStatus.REJECTED);
    }


    @Test
    void getBookingById_whenNotFoundBooking_thenExistenceExceptionThrow() {
        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> bookingService.getBookingById(1L, 1L));
        assertEquals(ex.getMessage(), "Бронирование с id=" + 1L + " не найдено в базе.");
    }

    @Test
    void getBookingById_whenItemsOwnerAndOwnerIsEqual_thenExistenceExceptionThrow() {
        Booking booking = createBooking();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        Long ownerId = 1L;

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> bookingService.getBookingById(ownerId, booking.getId()));

        assertEquals(ex.getMessage(), "Пользователь с id=" + ownerId +
                " не является ни собствеником предмета, ни создателем бронирования с id=" + booking.getId());
    }

    @Test
    void getBookingById_whenAllOk_thenReturnBookingResponseDto() {
        Booking booking = createBooking();
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        Long ownerId = 5L;

        BookingResponseDto actualBooking = bookingService.getBookingById(ownerId, booking.getId());

        assertEquals(actualBooking.getItem(), booking.getItem());
        assertEquals(actualBooking.getBooker(), booking.getBooker());
        assertEquals(actualBooking.getId(), booking.getId());
    }


    @Test
    void getBookingByOwner_whenUnknownState_thenValidationExceptionTrow() {
        PageRequest pageRequest = PageRequest.of(2,2);
        long bookerId = 1L;
        String satetString = "UnknownState";

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.getBookingByOwner(bookerId, satetString, pageRequest));

        assertEquals(ex.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingByOwner_whenBookingStateAll_thenReturnBookingResponseDtoWithBookingStateAll() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "ALL";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByItem_Owner(any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByOwner(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByOwner_whenBookingStateCurrent_thenReturnBookingResponseDtoWithBookingStateCurrent() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "CURRENT";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByItem_OwnerAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByOwner(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByOwner_whenBookingStatePast_thenReturnBookingResponseDtoWithBookingStatePast() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "PAST";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByItem_OwnerAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByOwner(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByOwner_whenBookingStateFuture_thenReturnBookingResponseDtoWithBookingStateFuture() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "FUTURE";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByItem_OwnerAndStartIsAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByOwner(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByOwner_whenBookingStateWaiting_thenReturnBookingResponseDtoWithBookingStateWaiting() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "WAITING";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByItem_OwnerAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByOwner(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByOwner_whenBookingStateRejected_thenReturnBookingResponseDtoWithBookingStateRejected() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "REJECTED";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByItem_OwnerAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByOwner(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByBooker_whenUnknownState_thenValidationExceptionTrow() {
        PageRequest pageRequest = PageRequest.of(2,2);
        long bookerId = 1L;
        String satetString = "UnknownState";

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.getBookingByBooker(bookerId, satetString, pageRequest));

        assertEquals(ex.getMessage(), "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingByBooker_whenBookingStateAll_thenReturnBookingResponseDtoWithBookingStateAll() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "ALL";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByBooker_Id(any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByBooker(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByBooker_whenBookingStateCurrent_thenReturnBookingResponseDtoWithBookingStateCurrent() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "CURRENT";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByBooker(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByBooker_whenBookingStatePast_thenReturnBookingResponseDtoWithBookingStatePast() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "PAST";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByBooker_IdAndEndIsBefore(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByBooker(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByBooker_whenBookingStateFuture_thenReturnBookingResponseDtoWithBookingStateFuture() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "FUTURE";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByBooker_IdAndStartIsAfter(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByBooker(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByBooker_whenBookingStateWaiting_thenReturnBookingResponseDtoWithBookingStateWaiting() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "WAITING";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByBooker_IdAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByBooker(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }

    @Test
    void getBookingByBooker_whenBookingStateRejected_thenReturnBookingResponseDtoWithBookingStateRejected() {
        PageRequest pageRequest = PageRequest.of(2,2);
        String stateString = "REJECTED";
        User user = createUser();
        Booking booking = createBooking();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        long ownerId = 1L;
        when(bookingRepository.findByBooker_IdAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking));


        List<BookingResponseDto> actualBooking = bookingService
                .getBookingByBooker(ownerId, stateString, pageRequest);

        assertEquals(actualBooking.get(0).getId(), booking.getId());
    }
}