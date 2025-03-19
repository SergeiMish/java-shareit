package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class ItemDto {

    @NotBlank
    String name;

    @NotNull
    String description;

    @NotNull
    Boolean available;

    Long requestId;
}
