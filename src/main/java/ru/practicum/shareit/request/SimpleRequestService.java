package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.SimpleRequestDto;

import java.util.List;

@Service
public interface SimpleRequestService {
    List<SimpleRequestDto> getUserRequests(Long userId);
}