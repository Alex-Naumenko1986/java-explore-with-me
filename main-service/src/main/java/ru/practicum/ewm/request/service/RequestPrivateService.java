package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestPrivateService {
    RequestDto createRequest(Integer userId, Integer eventId);

    List<RequestDto> getUserRequests(Integer userId);

    RequestDto cancelRequest(Integer userId, Integer requestId);
}
