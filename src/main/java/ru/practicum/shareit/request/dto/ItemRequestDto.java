package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Builder
@Getter
@Setter
@EqualsAndHashCode
public class ItemRequestDto {
    private Long id;
    @NotEmpty(message = "Description must be provided and cannot be empty")
    private String description;
    private LocalDateTime created;
    private List<ItemResponse> items;
}
