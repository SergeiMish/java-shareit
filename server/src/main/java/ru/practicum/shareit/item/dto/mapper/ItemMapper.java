package ru.practicum.shareit.item.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDtoRequestIdResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDtoRequestIdResponse mapItemToDtoRequest(Item item) {
        ItemDtoRequestIdResponse dto = new ItemDtoRequestIdResponse();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }

    public static List<ItemDtoRequestIdResponse> mapItemToDtoRequest(Iterable<Item> items) {
        List<ItemDtoRequestIdResponse> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapItemToDtoRequest(item));
        }
        return dtos;
    }
}