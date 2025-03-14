package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto,
            @RequestParam(required = false) Long requestId) {

        ItemDto createdItem = itemService.addItem(userId, itemDto, requestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(Constants.HEADER_USER_ID) Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        ItemDto updatedItem = itemService.updateItem(userId, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId, @RequestBody CommentDto commentDto, @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        CommentDto savedComment = itemService.addComment(userId, itemId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }
}