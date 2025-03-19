package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse create(@Valid @RequestBody ItemRequestDtoRequest dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.create(dto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> findUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse findRequestById(@PathVariable("requestId") Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findRequestById(requestId, userId);
    }

}