package ru.practicum.ewm.event.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.constant.Constants;
import ru.practicum.ewm.event.dto.AdminSearchRequestDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.enums.EventStatus;
import ru.practicum.ewm.event.service.admin.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/events")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService service;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Integer eventId, @RequestBody @Valid UpdateEventAdminRequestDto dto) {
        EventFullDto updatedEvent = service.updateEvent(eventId, dto);
        log.info("Event with id {} has been updated: {}", eventId, updatedEvent);
        return updatedEvent;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(@RequestParam(required = false) Integer[] users,
                                           @RequestParam(required = false) EventStatus[] states,
                                           @RequestParam(required = false) Integer[] categories,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        AdminSearchRequestDto dto = AdminSearchRequestDto.builder().users(users).states(states).categories(categories)
                .rangeStart(rangeStart).rangeEnd(rangeEnd).from(from).size(size).build();
        log.info("Searching events by params: {}", dto);
        return service.searchEvents(dto);
    }
}
