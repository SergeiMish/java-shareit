package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!item.isAvailable()) {
            throw new ItemUnavailableException("Item is not available for booking");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IllegalArgumentException("Start and end dates must be provided");
        }

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingDtoMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("User is not the owner");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingDtoMapper.toDto(updatedBooking);
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        return BookingDtoMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        List<Booking> bookings = bookingRepository.findBookingsByBookerId(userId);
        return filterBookingsByState(bookings, state);
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        List<Booking> bookings = bookingRepository.findBookingsByOwnerId(ownerId);

        if (bookings.isEmpty()) {
            throw new BookingNotFoundException("No bookings found for the given owner");
        }

        return filterBookingsByState(bookings, state);
    }

    private List<BookingDto> filterBookingsByState(List<Booking> bookings, String state) {
        return bookings.stream()
                .filter(booking -> {
                    switch (state.toUpperCase()) {
                        case "CURRENT":
                            return booking.getStart().isBefore(LocalDateTime.now()) && booking.getEnd().isAfter(LocalDateTime.now());
                        case "PAST":
                            return booking.getEnd().isBefore(LocalDateTime.now());
                        case "FUTURE":
                            return booking.getStart().isAfter(LocalDateTime.now());
                        case "WAITING":
                            return booking.getStatus() == BookingStatus.WAITING;
                        case "REJECTED":
                            return booking.getStatus() == BookingStatus.REJECTED;
                        default:
                            return true;
                    }
                })
                .map(BookingDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}