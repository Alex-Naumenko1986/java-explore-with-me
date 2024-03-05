package ru.practicum.ewm.event.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.constant.Constants;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UserSearchRequestDto;
import ru.practicum.ewm.event.enums.EventSort;
import ru.practicum.ewm.event.service.pub.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventPublicService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(name = "id") @NotNull @Min(1) Integer eventId,
                                     HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder().app("ewm-main-service")
                .uri(request.getRequestURI()).ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build();
        log.info("Getting event by id {}", eventId);
        EventFullDto event = service.getEventById(eventId, endpointHitDto);

        log.info("Received event: {}, endpoint hit saved: {}", event, endpointHitDto);

        return event;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> searchEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Integer[] categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                            HttpServletRequest request) {

        EndpointHitDto endpointHitDto = EndpointHitDto.builder().app("ewm-main-service")
                .uri(request.getRequestURI()).ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build();

        UserSearchRequestDto searchRequestDto = UserSearchRequestDto.builder().text(text).categories(categories)
                .paid(paid).onlyAvailable(onlyAvailable).rangeStart(rangeStart).rangeEnd(rangeEnd).sort(sort)
                .from(from).size(size).build();

        log.info("Searching events by params: {}", searchRequestDto);
        List<EventShortDto> events = service.searchEvents(searchRequestDto, endpointHitDto);
        log.info("Received list of events {} for search request {}", events, searchRequestDto);

        return events;
    }
}
