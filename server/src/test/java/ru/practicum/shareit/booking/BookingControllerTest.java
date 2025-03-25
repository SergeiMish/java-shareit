package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private Booking booking;
    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        owner = User.builder()
                .id(2L)
                .build();

        item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addBooking_success() throws Exception {
        given(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .willReturn(bookingDto);

        String requestBody = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Constants.HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void updateBookingStatus_success() throws Exception {
        BookingDto updatedBookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        given(bookingService.updateBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .willReturn(updatedBookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(Constants.HEADER_USER_ID, 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBooking_success() throws Exception {
        given(bookingService.getBooking(anyLong()))
                .willReturn(bookingDto);

        mockMvc.perform(get("/bookings/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getUserBookings_success() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        given(bookingService.getUserBookings(anyLong(), anyString()))
                .willReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(Constants.HEADER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getOwnerBookings_success() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        given(bookingService.getOwnerBookings(anyLong(), anyString()))
                .willReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(Constants.HEADER_USER_ID, 2L)
                        .param("state", "ALL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void addBooking_conflict() throws Exception {
        given(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .willThrow(new ConflictException("Booking conflict"));

        String requestBody = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(Constants.HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void updateBookingStatus_notFound() throws Exception {
        given(bookingService.updateBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .willThrow(new BookingNotFoundException("Booking not found"));

        mockMvc.perform(patch("/bookings/999")
                        .param("approved", "true")
                        .header(Constants.HEADER_USER_ID, 2L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}