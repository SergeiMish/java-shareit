package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ItemUnavailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private User user;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now();
        endDate = startDate.plusDays(2);
        user = User.builder()
                .id(1L)
                .build();

        User owner = User.builder()
                .id(2L)
                .build();

        item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .bookings(new HashSet<>())
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(startDate)
                .end(endDate)
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addBooking_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(itemRepository.findById(1L)).willReturn(Optional.of(item));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);

        BookingDto result = bookingService.addBooking(1L, bookingDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(startDate);
        assertThat(result.getEnd()).isEqualTo(endDate);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void addBooking_itemNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(ItemNotFoundException.class)
                .isThrownBy(() -> bookingService.addBooking(1L, bookingDto))
                .withMessage("Item not found");
    }

    @Test
    void addBooking_itemUnavailable() {
        item.setAvailable(false);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(itemRepository.findById(1L)).willReturn(Optional.of(item));

        assertThatExceptionOfType(ItemUnavailableException.class)
                .isThrownBy(() -> bookingService.addBooking(1L, bookingDto))
                .withMessage("Item is not available for booking");
    }

    @Test
    void updateBookingStatus_success() {
        given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);

        BookingDto result = bookingService.updateBookingStatus(1L, true, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void updateBookingStatus_accessDenied() {
        given(bookingRepository.findById(1L)).willReturn(Optional.of(booking));

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> bookingService.updateBookingStatus(1L, true, 1L))
                .withMessage("User is not the owner");
    }
}