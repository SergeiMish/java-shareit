package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                @RequestBody RequestDto dto) {
        return requestClient.createRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                 @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}