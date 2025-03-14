package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.RequestNotFoundException;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ItemRequestService itemRequestService;

    private final ItemRequestRepository itemRequestRepository;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestBody ItemRequestCreateDto requestDto) {
        ItemRequestDto savedRequest = itemRequestService.createRequest(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        ItemRequest request = itemRequestRepository.findByIdWithItems(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request not found"));

        return ResponseEntity.ok(ItemRequestMapper.toDto(request));
    }
}