package ru.practicum.shareit.request;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.ItemUnavailableException;
import ru.practicum.shareit.exeption.RequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemResponseDtoMapper;
import ru.practicum.shareit.request.mapper.SimpleRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.SimpleRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService, SimpleRequestService {
    private static final Logger logger = LoggerFactory.getLogger(ItemRequestServiceImpl.class);
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final SimpleRequestRepository simpleRequestRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto requestDto) {
        logger.info("Начало метода createRequest");

        // Находим пользователя
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Создаем новый простой запрос
        SimpleRequest simpleRequest = new SimpleRequest();
        simpleRequest.setDescription(requestDto.getDescription());
        simpleRequest.setCreated(LocalDateTime.now());
        simpleRequest.setRequester(requester);

        // Сохраняем простой запрос
        SimpleRequest savedSimpleRequest = simpleRequestRepository.save(simpleRequest);

        // Если указан ID предмета, связываем его с запросом
        if (requestDto.getItemId() != null) {
            Item item = itemRepository.findById(requestDto.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Item not found"));

            // Добавляем проверку доступности предмета
            if (!item.isAvailable()) {
                throw new ItemUnavailableException("Item is not available");
            }

            // Обновляем имя предмета в запросе
            savedSimpleRequest.setItemName(item.getName());
            simpleRequestRepository.save(savedSimpleRequest);
        }

        logger.info("Завершение метода createRequest");
        return SimpleRequestMapper.toDto(savedSimpleRequest);
    }

    @Override
    public List<SimpleRequestDto> getUserRequests(Long userId) {
        return simpleRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::convertToSimpleRequestDto)
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

    private SimpleRequestDto convertToSimpleRequestDto(SimpleRequest request) {
        SimpleRequestDto dto = new SimpleRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setRequesterId(request.getRequester().getId());
        dto.setItemName(request.getItemName());
        return dto;
    }
}