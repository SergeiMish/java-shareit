package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookItemRequestDto {

    @NotNull
    Long itemId;

    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @FutureOrPresent
    LocalDateTime end;
}