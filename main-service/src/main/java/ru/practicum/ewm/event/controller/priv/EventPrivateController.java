package ru.practicum.ewm.event.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.priv.EventPrivateService;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventPrivateService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Min(1) Integer userId, @RequestBody @Valid NewEventDto newEventDto) {
        EventFullDto createdEvent = service.createEvent(userId, newEventDto);
        log.info("New event has been created: {}", createdEvent);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @Min(1) Integer userId, @PathVariable @Min(1) Integer eventId) {
        EventFullDto event = service.getEventById(userId, eventId);
        log.info("Received event by id {}, {}", eventId, event);
        return event;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable @Min(1) Integer userId,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        List<EventShortDto> events = service.getUserEvents(userId, from, size);
        log.info("Received list of events, created by user with id {}, {}", userId, events);
        return events;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestsForUserEvent(@PathVariable @Min(1) Integer userId,
                                                    @PathVariable @Min(1) Integer eventId) {
        List<RequestDto> requests = service.getRequestsForUserEvent(userId, eventId);
        log.info("Received list of requests for event with id {}, {}", eventId, requests);
        return requests;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @Min(1) Integer userId,
                                    @PathVariable @Min(1) Integer eventId,
                                    @RequestBody @Valid UpdateEventUserRequestDto updateEventDto) {
        EventFullDto updatedEvent = service.updateEvent(userId, eventId, updateEventDto);
        log.info("Event with id {} has been updated: {}", eventId, updatedEvent);

        return updatedEvent;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusUpdateResultDto updateRequestStatus(@PathVariable @Min(1) Integer userId,
                                                            @PathVariable @Min(1) Integer eventId,
                                                            @RequestBody @Valid RequestStatusUpdateDto updateRequestDto) {
        RequestStatusUpdateResultDto updateResult = service.updateRequestStatus(userId, eventId, updateRequestDto);
        log.info("Requests have been updated: {}", updateResult);

        return updateResult;
    }
}
