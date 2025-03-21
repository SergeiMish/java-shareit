package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class ItemControllerTest {

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final String searchText = "test";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private ItemDto testItemDto;
    private CommentDto testCommentDto;

    @BeforeEach
    void setUp() {
        testItemDto = ItemDto.builder()
                .id(null)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .ownerId(userId)
                .build();

        testCommentDto = CommentDto.builder()
                .id(null)
                .text("Test comment")
                .authorName("Test Author")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserItems_shouldReturnItemsForUser() throws Exception {
        when(itemService.getUserItems(userId))
                .thenReturn(List.of(testItemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<ItemDto> dtos = objectMapper.readValue(json, new TypeReference<List<ItemDto>>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }

    @Test
    void addItem_success() throws Exception {
        ItemDto createdItem = ItemDto.builder()
                .id(2L)
                .name(testItemDto.getName())
                .description(testItemDto.getDescription())
                .available(testItemDto.getAvailable())
                .ownerId(userId)
                .build();

        given(itemService.addItem(userId, testItemDto)).willReturn(createdItem);

        String requestBody = objectMapper.writeValueAsString(testItemDto);
        String expectedResponse = objectMapper.writeValueAsString(createdItem);

        mockMvc.perform(post("/items")
                        .header(Constants.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void updateItem_success() throws Exception {
        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated name")
                .description("Updated description")
                .available(true)
                .ownerId(userId)
                .build();

        given(itemService.updateItem(userId, updatedItem)).willReturn(updatedItem);

        String requestBody = objectMapper.writeValueAsString(updatedItem);
        String expectedResponse = objectMapper.writeValueAsString(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getItem_success() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .ownerId(userId)
                .build();

        given(itemService.getItem(itemId, userId)).willReturn(itemDto);

        String expectedResponse = objectMapper.writeValueAsString(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(Constants.HEADER_USER_ID, userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void searchItems_success() throws Exception {
        List<ItemDto> searchResults = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Item with search text")
                        .description("Description containing search text")
                        .available(true)
                        .ownerId(userId)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Another search item")
                        .description("More search text here")
                        .available(false)
                        .ownerId(userId)
                        .build()
        );

        given(itemService.searchItems(searchText)).willReturn(searchResults);

        String expectedResponse = objectMapper.writeValueAsString(searchResults);

        mockMvc.perform(get("/items/search?text={searchText}", searchText))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void addComment_success() throws Exception {
        CommentDto createdComment = CommentDto.builder()
                .id(1L)
                .text(testCommentDto.getText())
                .authorName(testCommentDto.getAuthorName())
                .created(LocalDateTime.now())
                .build();

        given(itemService.addComment(userId, itemId, testCommentDto)).willReturn(createdComment);

        String requestBody = objectMapper.writeValueAsString(testCommentDto);
        String expectedResponse = objectMapper.writeValueAsString(createdComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getItem_notFound() throws Exception {
        given(itemService.getItem(itemId, userId)).willThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(Constants.HEADER_USER_ID, userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
