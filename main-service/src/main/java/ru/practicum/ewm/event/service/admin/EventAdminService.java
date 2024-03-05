package ru.practicum.ewm.event.service.admin;

import ru.practicum.ewm.event.dto.AdminSearchRequestDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequestDto;

import java.util.List;

public interface EventAdminService {
    EventFullDto updateEvent(Integer eventId, UpdateEventAdminRequestDto dto);

    List<EventFullDto> searchEvents(AdminSearchRequestDto dto);
}
