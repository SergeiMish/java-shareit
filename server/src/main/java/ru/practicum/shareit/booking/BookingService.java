package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingDto updateBookingStatus(Long bookingId, boolean approved, Long userId);

    BookingDto getBooking(Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String state);

    List<BookingDto> getOwnerBookings(Long ownerId, String state);
}