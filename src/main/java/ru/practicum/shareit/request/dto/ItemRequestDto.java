package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
    public class ItemRequestDto {
    private Long id;
    private String itemName;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
    private Long requesterId;
}