package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoRequestIdResponse;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestDtoRequest dto, User owner) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setOwner(owner);
        return request;
    }

    public static ItemRequestDtoResponse toDto(ItemRequest request) {
        ItemRequestDtoResponse dto = new ItemRequestDtoResponse();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static List<ItemRequestDtoResponse> toDto(Iterable<ItemRequest> requests) {
        List<ItemRequestDtoResponse> dtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            dtos.add(toDto(request));
        }
        return dtos;
    }

    public static ItemRequestDtoResponse toDto(ItemRequest request, List<Item> items) {
        List<ItemDtoRequestIdResponse> resultItems = ItemMapper.mapItemToDtoRequest(
                items.stream().filter(item -> Objects.equals(item.getRequest(), request)).toList());
        ItemRequestDtoResponse dto = new ItemRequestDtoResponse();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(resultItems);
        return dto;
    }

    public static List<ItemRequestDtoResponse> toDto(Iterable<ItemRequest> requests, List<Item> items) {
        List<ItemRequestDtoResponse> dtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> requestItems = items.stream().filter(item -> item.getRequest().equals(request)).toList();
            dtos.add(toDto(request, requestItems));
        }
        return dtos;
    }

}