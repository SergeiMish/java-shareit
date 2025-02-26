package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, ItemDto> items = new HashMap<>();
    private long idCounter = 0;

    @Override
    public ItemDto save(ItemDto item) {
        if (item.getId() == null) {
            item.setId(++idCounter);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<ItemDto> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<ItemDto> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }

    @Override
    public List<ItemDto> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
