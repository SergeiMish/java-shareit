package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;
}
