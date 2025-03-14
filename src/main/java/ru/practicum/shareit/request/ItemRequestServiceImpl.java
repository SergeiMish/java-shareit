package ru.practicum.shareit.request;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.RequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemResponseDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final Logger logger = LoggerFactory.getLogger(ItemRequestServiceImpl.class);
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemResponseDtoMapper itemResponseDtoMapper;
    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto requestDto) {
        logger.info("Начало метода createRequest");

        // Находим пользователя
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Создаем новый запрос
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setCreated(LocalDateTime.now());
        request.setRequester(requester);

        // Сохраняем запрос
        ItemRequest savedRequest = itemRequestRepository.save(request);

        // Если указан ID предмета, связываем его с запросом
        if (requestDto.getItemId() != null) {
            Item item = itemRepository.findById(requestDto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found"));

            item.setRequest(savedRequest);
            savedRequest.getItems().add(item);
            savedRequest.setItemName(item.getName());

            itemRequestRepository.save(savedRequest);
        }

        logger.info("Завершение метода createRequest");
        return ItemRequestMapper.toDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        return itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::convertToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        return itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(this::convertToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId) {
        return itemRequestRepository.findByIdWithItems(requestId)
                .map(this::convertToItemRequestDto)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
    }

    private ItemRequestDto convertToItemRequestDto(ItemRequest request) {
        ItemRequestDto dto = itemRequestMapper.toDto(request);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            String itemName = dto.getItems().get(0).getName();
            if (!itemName.equals(dto.getItemName())) {
                request.setItemName(itemName);
                itemRequestRepository.save(request);
                dto.setItemName(itemName);
            }
        }

        return dto;
    }
}

