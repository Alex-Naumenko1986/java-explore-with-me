package ru.practicum.ewm.event.service.pub;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UserSearchRequestDto;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.entity.QEventEntity;
import ru.practicum.ewm.event.enums.EventSort;
import ru.practicum.ewm.event.enums.EventStatus;
import ru.practicum.ewm.event.exception.InvalidDateRangeException;
import ru.practicum.ewm.event.mapper.FullEventMapper;
import ru.practicum.ewm.event.mapper.ShortEventMapper;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.event.utils.EventFieldSetter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventFieldSetter eventFieldSetter;
    private final FullEventMapper fullEventMapper;
    private final ShortEventMapper shortEventMapper;

    @Override
    public EventFullDto getEventById(Integer eventId, EndpointHitDto endpointHitDto) {
        EventEntity eventEntity = eventRepository.findByIdAndState(eventId, EventStatus.PUBLISHED);

        if (eventEntity == null) {
            throw new NotFoundException(String.format(String.format("Event with id=%d was not found", eventId)));
        }

        eventFieldSetter.setConfirmedRequests(eventEntity);
        eventFieldSetter.setViews(eventEntity);

        log.info("Saving endpoint hit: {}", endpointHitDto);
        statsClient.createEndpointHit(endpointHitDto);

        EventFullDto eventFullDto = fullEventMapper.toDto(eventEntity);

        log.info("Event has been received from database: {}", eventFullDto);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> searchEvents(UserSearchRequestDto dto, EndpointHitDto endpointHitDto) {
        if (dto.getRangeStart() != null && dto.getRangeEnd() != null && dto.getRangeStart().isAfter(dto.getRangeEnd())) {
            throw new InvalidDateRangeException("RangeStart should be before RangeEnd");
        }

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(QEventEntity.eventEntity.state.eq(EventStatus.PUBLISHED));

        if (dto.getText() != null) {
            predicates.add(QEventEntity.eventEntity.annotation.containsIgnoreCase(dto.getText())
                    .or(QEventEntity.eventEntity.description.containsIgnoreCase(dto.getText())));
        }

        if (dto.getCategories() != null) {
            predicates.add(QEventEntity.eventEntity.category.id.in(dto.getCategories()));
        }

        if (dto.getPaid() != null) {
            predicates.add(QEventEntity.eventEntity.paid.eq(dto.getPaid()));
        }

        if (dto.getRangeStart() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.after(dto.getRangeStart()));
        }

        if (dto.getRangeEnd() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.before(dto.getRangeEnd()));
        }

        if (dto.getRangeStart() == null && dto.getRangeEnd() == null) {
            predicates.add(QEventEntity.eventEntity.eventDate.after(LocalDateTime.now()));
        }

        Predicate predicate = ExpressionUtils.allOf(predicates);

        Iterable<EventEntity> foundEventsAsIterable = eventRepository.findAll(predicate);
        List<EventEntity> foundEvents = new ArrayList<>();
        foundEventsAsIterable.forEach(foundEvents::add);

        eventFieldSetter.setViews(foundEvents);
        eventFieldSetter.setConfirmedRequests(foundEvents);

        if (dto.getOnlyAvailable()) {
            foundEvents = foundEvents.stream()
                    .filter(eventEntity -> eventEntity.getParticipantLimit() == 0
                            || eventEntity.getParticipantLimit() > eventEntity.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        if (dto.getSort() == EventSort.EVENT_DATE) {
            foundEvents = foundEvents.stream().sorted(Comparator.comparing(EventEntity::getEventDate))
                    .skip(dto.getFrom()).limit(dto.getSize()).collect(Collectors.toList());
        } else {
            foundEvents = foundEvents.stream().sorted(Comparator.comparing(EventEntity::getViews))
                    .skip(dto.getFrom()).limit(dto.getSize()).collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtos = foundEvents.stream().map(shortEventMapper::toDto)
                .collect(Collectors.toList());

        log.info("List of events has been received from database: {}. For request: {}", eventShortDtos, dto);

        statsClient.createEndpointHit(endpointHitDto);

        log.info("Endpoint hit saved: {}", endpointHitDto);

        return eventShortDtos;
    }
}
