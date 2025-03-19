package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constants.Constants;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                             @RequestBody @Valid BookItemRequestDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam Boolean approved) {
        log.info("Updating booking status for {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId) {
        log.info("Get booking {}", bookingId);
        return bookingClient.getBooking(bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "all") String state) {
        log.info("Get user bookings with state {}, userId={}", state, userId);
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(Constants.HEADER_USER_ID) Long ownerId,
                                                   @RequestParam(defaultValue = "all") String state) {
        log.info("Get owner bookings with state {}, ownerId={}", state, ownerId);
        return bookingClient.getOwnerBookings(ownerId, state);
    }
}