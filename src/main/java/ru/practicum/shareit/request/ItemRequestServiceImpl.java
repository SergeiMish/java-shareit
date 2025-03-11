package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        ItemRequest request = ItemRequest.builder()
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(userRepository.getOne(userId))
                .build();

        return convertToItemRequestDto(itemRequestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        return itemRequestRepository.findByRequesterId(userId)
                .stream()
                .map(this::convertToItemRequestDtoWithItems)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        return null;
    }

    private ItemRequestDto convertToItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    private ItemRequestDto convertToItemRequestDtoWithItems(ItemRequest request) {
        ItemRequestDto dto = convertToItemRequestDto(request);
        dto.setItems(itemRepository.findByRequestId(request.getId())
                .stream()
                .map(item -> ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.isAvailable())
                        .ownerId(item.getOwner().getId())
                        .build())
                .collect(Collectors.toList()));
        return dto;
    }
}