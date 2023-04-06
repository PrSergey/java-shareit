package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.constant.statusBooking;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    private Long id;
    @NotNull
    private Long itemId;
    @NotNull
    private Long ownerId;
    @Future
    private LocalDate start;
    @Future
    private LocalDate end;
    @NotNull
    private statusBooking confirmation;


}
