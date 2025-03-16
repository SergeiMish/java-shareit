package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
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
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoResponse create(ItemRequestDtoRequest dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Пользователь " + userId + " не найден"));

        ItemRequest request = itemRequestRepository.save(ItemRequestMapper.toEntity(dto, user));
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDtoResponse> findUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не найден");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByOwnerIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<Item> allItems = itemRepository.findAllByRequestId(requestIds);

        return ItemRequestMapper.toDto(requests, allItems);
    }

    @Override
    public List<ItemRequestDtoResponse> findAllRequests(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByOwnerIdNotOrderByCreatedDesc(userId);
        return ItemRequestMapper.toDto(itemRequests);
    }


    @Override
    public ItemRequestDtoResponse findRequestById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь " + userId + " не найден");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос с id = " + requestId + " не найден"));

        List<Item> allItems = itemRepository.findAllByRequestId(List.of(requestId));

        return ItemRequestMapper.toDto(itemRequest, allItems);
    }

}