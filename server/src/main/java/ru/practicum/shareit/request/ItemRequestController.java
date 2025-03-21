package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.constants.Constants;
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
    public ResponseEntity<ItemRequestDtoResponse> create(
            @Valid @RequestBody ItemRequestDtoRequest dto,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {

        ItemRequestDtoResponse response = itemRequestService.create(dto, userId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> findUserRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemRequestService.findUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAllRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemRequestService.findAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse findRequestById(@PathVariable("requestId") Long requestId,
                                                  @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemRequestService.findRequestById(requestId, userId);
    }

}