package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.constants.Constants;


import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestBody BookingDto bookingDto, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        BookingDto createdBooking = bookingService.addBooking(userId, bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable Long bookingId, @RequestParam boolean approved, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return bookingService.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(Constants.HEADER_USER_ID) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(Constants.HEADER_USER_ID) Long ownerId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }
}