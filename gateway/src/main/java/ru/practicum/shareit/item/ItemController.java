package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comments")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                             @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                          @RequestBody ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available status must be provided");
        }
        if (itemDto.getName() == null || itemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be provided and cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description must be provided and cannot be empty");
        }
        return itemClient.addItem(userId, itemDto);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}

