package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    ItemDto save(ItemDto item);

    Optional<ItemDto> findById(Long id);

    List<ItemDto> findAll();

    void deleteById(Long id);

    List<ItemDto> findByOwnerId(Long ownerId);
}
