package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        List<ItemResponseDto> items = itemRequest.getItems().stream()
                .map(ItemDtoMapper::toItemDto)
                .map(ItemResponseDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .itemName(itemRequest.getItemName())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toEntity(String description, User user, List<Item> items) {
        String itemName = items.isEmpty() ? "Default Item Name" : items.get(0).getName();

        ItemRequest itemRequest = ItemRequest.builder()
                .itemName(itemName)
                .description(description)
                .created(LocalDateTime.now())
                .requester(user)
                .build();

        for (Item item : items) {
            item.setRequest(itemRequest);
        }

        itemRequest.setItems(items);

        return itemRequest;
    }
}