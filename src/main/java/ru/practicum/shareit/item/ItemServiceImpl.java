package ru.practicum.shareit.item;

import jakarta.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.RequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
     @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Long requestId = itemDto.getRequestId();
        validateItemDto(itemDto);
        logger.info("Поиск пользователя с ID: {}", userId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ItemRequest request = null;
        if (requestId != null) {
            logger.info("Поиск запроса с ID: {}", requestId);
            request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RequestNotFoundException("Request not found"));
        }

        Item item = new Item();
        item.setOwner(owner);
        item.setRequest(request);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        Item savedItem = itemRepository.save(item);
        return ItemDtoMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Long itemId = itemDto.getId();
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("User is not the owner");
        }
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(existingItem);
        return ItemDtoMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        boolean isOwner = item.getOwner().getId().equals(userId);

        Optional<Booking> lastBookingOpt = isOwner ? bookingRepository.findLastBookingByItemId(item.getId()) : Optional.empty();
        Optional<Booking> nextBookingOpt = bookingRepository.findNextBookingByItemId(item.getId());

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .authorName(comment.getUser().getName())
                        .created(comment.getCreated())
                        .build())
                .collect(Collectors.toList());

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(lastBookingOpt.map(BookingDtoMapper::toDto).orElse(null))
                .nextBooking(nextBookingOpt.map(BookingDtoMapper::toDto).orElse(null))
                .comments(commentDtos)
                .build();
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.findItemsWithBookingsAndCommentsByOwnerId(userId).stream()
                .map(item -> {
                    Optional<Booking> lastBookingOpt = item.getBookings().stream()
                            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                            .max(Comparator.comparing(Booking::getEnd));

                    Optional<Booking> nextBookingOpt = item.getBookings().stream()
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .min(Comparator.comparing(Booking::getStart));

                    List<CommentDto> commentDtos = item.getComments().stream()
                            .map(CommentDtoMapper::toDto)
                            .collect(Collectors.toList());

                    return ItemDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .available(item.isAvailable())
                            .lastBooking(lastBookingOpt.map(BookingDtoMapper::toDto).orElse(null))
                            .nextBooking(nextBookingOpt.map(BookingDtoMapper::toDto).orElse(null))
                            .comments(commentDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.isAvailable()) &&
                        ((item.getName() != null && item.getName().toLowerCase().contains(lowerCaseText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseText))))
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        boolean isApprovedAndCompleted = bookingRepository.findBookingsByBookerId(userId).stream()
                .anyMatch(booking -> booking.getItem().getId().equals(itemId) &&
                        booking.getStatus() == BookingStatus.APPROVED &&
                        booking.getEnd().isBefore(LocalDateTime.now()));

        logger.info("Checking booking for itemId: {}, userId: {}, status: APPROVED and completed", itemId, userId);
        if (!isApprovedAndCompleted) {
            logger.warn("User has not booked this item with APPROVED status or the booking is not completed");
            throw new IllegalArgumentException("User has not booked this item with APPROVED status or the booking is not completed");
        }

        Comment comment = CommentDtoMapper.toEntity(commentDto, item, user);
        Comment savedComment = commentRepository.save(comment);
        return CommentDtoMapper.toDto(savedComment);
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available status must be provided");
        }
        if (itemDto.getName() == null || itemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be provided and cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description must be provided and cannot be empty");
        }
    }
}
