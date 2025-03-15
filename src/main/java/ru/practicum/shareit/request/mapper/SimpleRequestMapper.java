package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.SimpleRequest;

@Component
public class SimpleRequestMapper {
    public static ItemRequestDto toDto(SimpleRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setRequesterId(request.getRequester().getId());
        dto.setItemName(request.getItemName());
        return dto;
    }
}