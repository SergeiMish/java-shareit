package ru.practicum.shareit.request.dto;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestCreateDto {
    private String description;
    private Long itemId;
}