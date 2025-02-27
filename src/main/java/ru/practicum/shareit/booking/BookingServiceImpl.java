package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
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
    public BookingDto updateBookingStatus(Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
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