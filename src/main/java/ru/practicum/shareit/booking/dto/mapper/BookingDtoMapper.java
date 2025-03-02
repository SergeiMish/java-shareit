package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class BookingDtoMapper {

    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        UserDto bookerDto = (booking.getBooker() != null) ? UserDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build() : null;

        ItemDto itemDto = (booking.getItem() != null) ? ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build() : null;

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(bookerDto)
                .item(itemDto)
                .build();
    }

    public static Booking toEntity(BookingDto bookingDto, Item item, User booker) {
        if (bookingDto == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());

        return booking;
    }
}
