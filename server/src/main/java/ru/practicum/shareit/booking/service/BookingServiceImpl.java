package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constant.BookingState;
import ru.practicum.shareit.constant.BookingStatus;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingResponseDto save(Long userId, BookingRequestDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ExistenceException("Предмет с id=" + bookingDto.getItemId() + " не найден в базе."));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException("Пользвателя с id=" + userId + " не найден в базе."));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id=" + bookingDto.getItemId() + "не доступна для аренды");
        }
        if (userId.equals(item.getOwner())) {
            throw new ExistenceException("Владелец не может бронировать собственный предмет");
        }

        //Booking booking = new Booking(item, booker, bookingDto.getStart(), bookingDto.getEnd(), BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.fromBookingDto(bookingDto, item, booker)));
    }

    @Transactional
    @Override
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, Boolean approve) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ExistenceException("Бронирование с id=" + bookingId + " не найдено в базе."));
        if (Objects.equals(booking.getBooker().getId(), ownerId)) {
            throw new ExistenceException("Пользователь с id=" + ownerId +
                    " не может поменять статус, так как он арендатор.");
        }
        if (!Objects.equals(booking.getItem().getOwner(), ownerId)) {
            throw new ValidationException("Пользователь с id=" + ownerId +
                    " не является собствеником вещи с id=" + booking.getItem().getId());
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("У бронирования с id=" + bookingId +
                    " уже подтвержден статус");
        }

        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ExistenceException("Бронирование с id=" + bookingId + " не найдено в базе."));
        if (!Objects.equals(booking.getItem().getOwner(), userId)
                && !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new ExistenceException("Пользователь с id=" + userId +
                    " не является ни собствеником предмета, ни создателем бронирования с id=" + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingByBooker(Long bookerId, String stateString, PageRequest pageRequest) {
        BookingState state = getState(stateString);
        checkingPresenceOfUser(bookerId);
        LocalDateTime nowTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        if (Objects.equals(state, BookingState.ALL)) {
            bookings = bookingRepository.findByBooker_Id(bookerId, Sort.by(Sort.Direction.DESC,
                    "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.CURRENT)) {
            bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(bookerId,
                    nowTime, nowTime, Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.PAST)) {
            bookings = bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, nowTime,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.FUTURE)) {
            bookings = bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, nowTime,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.WAITING)) {
            bookings = bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.WAITING,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.REJECTED)) {
            bookings = bookingRepository.findByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingByOwner(Long ownerId, String stateString, PageRequest pageRequest) {
        BookingState state = getState(stateString);
        checkingPresenceOfUser(ownerId);
        LocalDateTime nowTime = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        if (Objects.equals(state, BookingState.ALL)) {
            bookings = bookingRepository.findByItem_Owner(ownerId, Sort.by(Sort.Direction.DESC,
                    "start"), pageRequest);
        } else if (Objects.equals(state, BookingState.CURRENT)) {
            bookings = bookingRepository.findByItem_OwnerAndStartIsBeforeAndEndIsAfter(ownerId,
                    nowTime, nowTime, Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.PAST)) {
            bookings = bookingRepository.findByItem_OwnerAndEndIsBefore(ownerId, nowTime,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.FUTURE)) {
            bookings = bookingRepository.findByItem_OwnerAndStartIsAfter(ownerId, nowTime,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.WAITING)) {
            bookings = bookingRepository.findByItem_OwnerAndStatus(ownerId, BookingStatus.WAITING,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        } else if (Objects.equals(state, BookingState.REJECTED)) {
            bookings = bookingRepository.findByItem_OwnerAndStatus(ownerId, BookingStatus.REJECTED,
                    Sort.by(Sort.Direction.DESC, "end"), pageRequest);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }


    private BookingState getState(String stateString) {
        return BookingState.valueOf(stateString);
    }

    private void checkingPresenceOfUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ExistenceException("Пользвателя с id=" + userId + " не найден в базе.");
        }
    }

}
