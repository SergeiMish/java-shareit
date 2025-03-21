package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.RequestNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    private Long userId;
    private Long requestId;
    private ItemRequestDtoRequest requestDtoRequest;
    private ItemRequestDtoResponse requestDtoResponse;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        userId = 1L;
        requestId = 1L;

        requestDtoRequest = ItemRequestDtoRequest.builder()
                .description("Test request")
                .build();

        requestDtoResponse = ItemRequestDtoResponse.builder()
                .id(requestId)
                .description("Test request")
                .created(LocalDateTime.now())
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Test request");
        itemRequest.setCreated(LocalDateTime.now());

        item = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Test item description")
                .available(true)
                .build();
    }

    @Test
    void create_shouldCreateRequest() {
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDtoResponse result = service.create(requestDtoRequest, userId);

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(any(ItemRequest.class));

        assertEquals(requestId, result.getId());
        assertEquals("Test request", result.getDescription());
    }

    @Test
    void findUserRequests_shouldFindRequests() {
        User user = User.builder().id(userId).build();

        item.setOwner(user);
        item.setRequest(itemRequest);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByOwnerIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyList()))
                .thenReturn(List.of(item));

        List<ItemRequestDtoResponse> result = service.findUserRequests(userId);

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findAllByOwnerIdOrderByCreatedDesc(userId);
        verify(itemRepository).findAllByRequestId(anyList());

        assertEquals(1, result.size());
        assertEquals(requestId, result.get(0).getId());
    }

    @Test
    void findAllRequests_shouldFindAllRequests() {
        when(itemRequestRepository.findByOwnerIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDtoResponse> result = service.findAllRequests(userId);

        verify(itemRequestRepository).findByOwnerIdNotOrderByCreatedDesc(userId);

        assertEquals(1, result.size());
        assertEquals(requestId, result.get(0).getId());
    }

    @Test
    void findRequestById_shouldFindRequest() {
        User user = User.builder().id(userId).build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyList()))
                .thenReturn(List.of(item));

        ItemRequestDtoResponse result = service.findRequestById(requestId, userId);

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findAllByRequestId(anyList());

        assertEquals(requestId, result.getId());
        assertEquals("Test request", result.getDescription());
    }

    @Test
    void create_shouldThrowUserNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.create(requestDtoRequest, userId));

        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void findRequestById_shouldThrowRequestNotFoundException() {
        when(userRepository.existsById(userId)).thenReturn(true);

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class,
                () -> service.findRequestById(requestId, userId));

        verify(userRepository).existsById(userId);
        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository, never()).findAllByRequestId(anyList());
    }
}