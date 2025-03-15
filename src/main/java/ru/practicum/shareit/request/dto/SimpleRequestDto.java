package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Long requesterId;
    private String itemName;
}
