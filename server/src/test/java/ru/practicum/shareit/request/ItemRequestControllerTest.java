package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.constants.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@RequiredArgsConstructor
class ItemRequestControllerTest {

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final ItemRequestDtoRequest requestDtoRequest = ItemRequestDtoRequest.builder()
            .description("Test request")
            .build();
    private final ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoResponse.builder()
            .id(requestId)
            .description("Test request")
            .build();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void create_shouldCreateRequest() throws Exception {
        ItemRequestDtoRequest dto = ItemRequestDtoRequest.builder()
                .description("Test description")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/requests")
                        .header(Constants.HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void findUserRequests_shouldFindRequests() throws Exception {
        when(itemRequestService.findUserRequests(userId))
                .thenReturn(List.of(itemRequestDtoResponse));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId));

        verify(itemRequestService).findUserRequests(userId);
    }

    @Test
    void findAllRequests_shouldFindAllRequests() throws Exception {
        when(itemRequestService.findAllRequests(userId))
                .thenReturn(List.of(itemRequestDtoResponse));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId));

        verify(itemRequestService).findAllRequests(userId);
    }

    @Test
    void findRequestById_shouldFindRequest() throws Exception {
        when(itemRequestService.findRequestById(requestId, userId))
                .thenReturn(itemRequestDtoResponse);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId));

        verify(itemRequestService).findRequestById(requestId, userId);
    }
}
