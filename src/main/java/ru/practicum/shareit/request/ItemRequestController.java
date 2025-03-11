package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.net.URI;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemRequestDto requestDto) {
        ItemRequestDto createdRequest = itemRequestService.createRequest(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemRequestService.getRequest(requestId, userId);
    }
}