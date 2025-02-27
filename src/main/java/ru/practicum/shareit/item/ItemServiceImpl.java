package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available status must be provided");
        }
        if (itemDto.getName() == null || itemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be provided and cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description must be provided and cannot be empty");
        }
        Item item = ItemDtoMapper.toItem(itemDto);
        item.setOwnerId(userId);
        Item savedItem = itemRepository.save(item);
        return ItemDtoMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Long itemId = itemDto.getId();
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (!existingItem.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("User is not the owner");
        }
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(existingItem);
        return ItemDtoMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) &&
                        ((item.getName() != null && item.getName().toLowerCase().contains(lowerCaseText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseText))))
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }
}