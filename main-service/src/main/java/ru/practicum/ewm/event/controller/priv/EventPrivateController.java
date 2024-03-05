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
import javax.validation.constraints.NotNull;
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
    public EventFullDto createEvent(@PathVariable @NotNull @Min(1) Integer userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Creating new event: {} by user with id {}", newEventDto, userId);
        EventFullDto createdEvent = service.createEvent(userId, newEventDto);
        log.info("New event has been created: {}", createdEvent);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @NotNull @Min(1) Integer userId,
                                     @PathVariable @NotNull @Min(1) Integer eventId) {
        log.info("Getting event with id {} by user with id {}", eventId, userId);
        EventFullDto event = service.getEventById(userId, eventId);
        log.info("Received event by id {}, {}", eventId, event);
        return event;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable @NotNull @Min(1) Integer userId,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Getting events created by user with id {}, from {}, size {}", userId, from, size);
        List<EventShortDto> events = service.getUserEvents(userId, from, size);
        log.info("Received list of events, created by user with id {}, {}", userId, events);
        return events;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestsForUserEvent(@PathVariable @NotNull @Min(1) Integer userId,
                                                    @PathVariable @NotNull @Min(1) Integer eventId) {
        log.info("Getting participation requests for event with id {}, created by user with id {}", eventId, userId);
        List<RequestDto> requests = service.getRequestsForUserEvent(userId, eventId);
        log.info("Received list of requests for event with id {}, {}", eventId, requests);
        return requests;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable @NotNull @Min(1) Integer userId,
                                    @PathVariable @NotNull @Min(1) Integer eventId,
                                    @RequestBody @Valid UpdateEventUserRequestDto updateEventDto) {
        log.info("Updating event with id {} by user with id {}, {}", eventId, userId, updateEventDto);
        EventFullDto updatedEvent = service.updateEvent(userId, eventId, updateEventDto);
        log.info("Event with id {} has been updated: {}", eventId, updatedEvent);

        return updatedEvent;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestStatusUpdateResultDto updateRequestStatus(@PathVariable @NotNull @Min(1) Integer userId,
                                                            @PathVariable @NotNull @Min(1) Integer eventId,
                                                            @RequestBody @Valid RequestStatusUpdateDto updateRequestDto) {
        log.info("Updating request statuses for event with id {}, {}", eventId, updateRequestDto);
        RequestStatusUpdateResultDto updateResult = service.updateRequestStatus(userId, eventId, updateRequestDto);
        log.info("Requests have been updated: {}", updateResult);

        return updateResult;
    }
}
