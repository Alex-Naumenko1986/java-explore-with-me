package ru.practicum.ewm.event.service.pub;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UserSearchRequestDto;

import java.util.List;

public interface EventPublicService {
    EventFullDto getEventById(Integer eventId, EndpointHitDto endpointHitDto);

    List<EventShortDto> searchEvents(UserSearchRequestDto dto, EndpointHitDto endpointHitDto);
}
