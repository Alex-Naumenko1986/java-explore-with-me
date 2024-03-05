package ru.practicum.ewm.event.service.priv;


import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface EventPrivateService {
    EventFullDto createEvent(Integer userId, NewEventDto newEventDto);

    EventFullDto getEventById(Integer userId, Integer eventId);

    List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size);

    List<RequestDto> getRequestsForUserEvent(Integer userId, Integer eventId);

    EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventUserRequestDto dto);

    RequestStatusUpdateResultDto updateRequestStatus(Integer userId, Integer eventId, RequestStatusUpdateDto dto);
}
