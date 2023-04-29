package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                                               LocalDateTime start,
                                                                               LocalDateTime end,
                                                                               Sort sort);

    List<Booking> findByItem_Owner(Long ownerId, Sort sort);

    List<Booking> findByItem_OwnerAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_OwnerAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_OwnerAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItem_OwnerAndStartIsBeforeAndEndIsAfter(Long ownerId,
                                                                               LocalDateTime start,
                                                                               LocalDateTime end,
                                                                               Sort sort);

    List<Booking> findByItem_IdAndStartIsBeforeAndStatus(Long itemId, LocalDateTime end,
                                                       BookingStatus status,  Sort sort);

    List<Booking> findByItem_IdAndStartIsAfterAndStatus(Long itemId, LocalDateTime start,
                                                        BookingStatus status, Sort sort);

    List<Booking> findByItem_IdAndBooker_IdAndEndIsBefore(Long itemId, Long bookerId, LocalDateTime end);

    List<Booking> findByItem_OwnerAndStartIsBeforeAndStatus(Long ownerId, LocalDateTime start,
                                                        BookingStatus status, Sort sort);

    List<Booking> findByItem_OwnerAndStartIsAfterAndStatus(Long ownerId, LocalDateTime start,
                                                        BookingStatus status, Sort sort);

}

