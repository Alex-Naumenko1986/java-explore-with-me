package ru.practicum.ewm.event.service.admin;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.entity.CategoryEntity;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.AdminSearchRequestDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.entity.LocationEntity;
import ru.practicum.ewm.event.entity.QEventEntity;
import ru.practicum.ewm.event.enums.EventStatus;
import ru.practicum.ewm.event.exception.IllegalEventOperationException;
import ru.practicum.ewm.event.exception.InvalidEventStartTimeException;
import ru.practicum.ewm.event.mapper.FullEventMapper;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.event.storage.LocationRepository;
import ru.practicum.ewm.event.utils.EventFieldSetter;
import ru.practicum.ewm.pageable.CustomPageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final EventFieldSetter eventFieldSetter;
    private final FullEventMapper fullEventMapper;

    @Override
    @Transactional
    public EventFullDto updateEvent(Integer eventId, UpdateEventAdminRequestDto dto) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        EventStatus eventStatus = eventEntity.getState();

        eventEntity.setTitle(Objects.requireNonNullElse(dto.getTitle(), eventEntity.getTitle()));
        eventEntity.setDescription(Objects.requireNonNullElse(dto.getDescription(), eventEntity.getDescription()));
        eventEntity.setAnnotation(Objects.requireNonNullElse(dto.getAnnotation(), eventEntity.getAnnotation()));
        eventEntity.setPaid(Objects.requireNonNullElse(dto.getPaid(), eventEntity.getPaid()));
        eventEntity.setParticipantLimit(Objects.requireNonNullElse(dto.getParticipantLimit(),
                eventEntity.getParticipantLimit()));
        eventEntity.setRequestModeration(Objects.requireNonNullElse(dto.getRequestModeration(),
                eventEntity.getRequestModeration()));


        if (dto.getStateAction() != null && eventStatus != EventStatus.PENDING) {
            throw new IllegalEventOperationException("Event status must be PENDING");
        }

        if (dto.getEventDate() != null && eventEntity.getState() == EventStatus.PUBLISHED
                && !eventEntity.getPublishedOn().plusHours(1).isBefore(dto.getEventDate())) {
            throw new InvalidEventStartTimeException("Event should start at time, which is not earlier than " +
                    "one hour after publication date");
        }

        if (dto.getEventDate() != null && !dto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventStartTimeException("Event should start at time, which is not earlier than " +
                    "two hours after current moment");
        }

        eventEntity.setEventDate(Objects.requireNonNullElse(dto.getEventDate(), eventEntity.getEventDate()));

        if (dto.getCategory() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found",
                            dto.getCategory())));
            eventEntity.setCategory(category);
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!eventEntity.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                        throw new InvalidEventStartTimeException("Event should start at time, which is not earlier than " +
                                "one hour after publication date");
                    }
                    eventEntity.setState(EventStatus.PUBLISHED);
                    eventEntity.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    eventEntity.setState(EventStatus.CANCELED);
            }
        }

        if (dto.getLocation() != null) {
            LocationEntity location = eventEntity.getLocation();
            location.setLon(dto.getLocation().getLon());
            location.setLat(dto.getLocation().getLat());
            locationRepository.save(location);
            eventEntity.setLocation(location);
        }

        eventEntity = eventRepository.save(eventEntity);
        eventFieldSetter.setViews(eventEntity);
        eventFieldSetter.setConfirmedRequests(eventEntity);

        EventFullDto eventFullDto = fullEventMapper.toDto(eventEntity);

        log.info("Event has been updated in database: {}", eventFullDto);

        return eventFullDto;
    }

    @Override
    @Transactional
    public List<EventFullDto> searchEvents(AdminSearchRequestDto dto) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(Expressions.asBoolean(true).isTrue());

        if (dto.getUsers() != null) {
            predicates.add(QEventEntity.eventEntity.initiator.id.in(dto.getUsers()));
        }

        if (dto.getCategories() != null) {
            predicates.add(QEventEntity.eventEntity.category.id.in(dto.getCategories()));
        }

        if (dto.getStates() != null) {
            predicates.add(QEventEntity.eventEntity.state.in(dto.getStates()));
        }

        if (dto.getRangeStart() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.after(dto.getRangeStart()));
        }

        if (dto.getRangeEnd() != null) {
            predicates.add(QEventEntity.eventEntity.eventDate.before(dto.getRangeEnd()));
        }

        Predicate predicate = ExpressionUtils.allOf(predicates);

        Pageable pageable = new CustomPageable(dto.getFrom(), dto.getSize(), Sort.by(Sort.Direction.ASC, "id"));
        List<EventEntity> eventEntities = eventRepository.findAll(predicate, pageable).toList();

        eventFieldSetter.setConfirmedRequests(eventEntities);
        eventFieldSetter.setViews(eventEntities);

        List<EventFullDto> foundEvents = eventEntities.stream().map(fullEventMapper::toDto).collect(Collectors.toList());
        log.info("List of events was founded: {}", foundEvents);
        return foundEvents;
    }
}
