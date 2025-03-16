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
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

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