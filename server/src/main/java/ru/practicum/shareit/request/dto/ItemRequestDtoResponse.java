package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDtoRequestIdResponse;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDtoResponse {
    Long id;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    LocalDateTime created;
    List<ItemDtoRequestIdResponse> items;
}