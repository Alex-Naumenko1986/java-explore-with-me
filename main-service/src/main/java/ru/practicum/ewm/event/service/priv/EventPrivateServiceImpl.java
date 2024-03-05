package ru.practicum.ewm.event.service.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.entity.CategoryEntity;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.entity.LocationEntity;
import ru.practicum.ewm.event.enums.EventStatus;
import ru.practicum.ewm.event.enums.RequestUpdateStatus;
import ru.practicum.ewm.event.exception.IllegalEventOperationException;
import ru.practicum.ewm.event.exception.InvalidEventStartTimeException;
import ru.practicum.ewm.event.mapper.FullEventMapper;
import ru.practicum.ewm.event.mapper.NewEventMapper;
import ru.practicum.ewm.event.mapper.ShortEventMapper;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.event.storage.LocationRepository;
import ru.practicum.ewm.event.utils.EventFieldSetter;
import ru.practicum.ewm.pageable.CustomPageable;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.entity.RequestEntity;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.exception.IllegalRequestOperationException;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.entity.UserEntity;
import ru.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {
    private final RequestMapper requestMapper;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final NewEventMapper newEventMapper;
    private final FullEventMapper fullEventMapper;
    private final ShortEventMapper shortEventMapper;
    private final EventFieldSetter eventFieldSetter;

    @Override
    @Transactional
    public EventFullDto createEvent(Integer userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventStartTimeException("Event should start at time, which is not earlier than " +
                    "two hours after current moment");
        }
        UserEntity initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        EventEntity eventEntity = newEventMapper.toEntity(newEventDto);
        eventEntity.setInitiator(initiator);

        Integer categoryId = eventEntity.getCategory().getId();
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", categoryId)));
        eventEntity.setCategory(category);

        LocationEntity location = locationRepository.save(eventEntity.getLocation());
        eventEntity.setLocation(location);

        eventEntity.setCreatedOn(LocalDateTime.now());
        eventEntity.setState(EventStatus.PENDING);

        eventEntity.setPaid(Objects.requireNonNullElse(eventEntity.getPaid(), false));
        eventEntity.setParticipantLimit(Objects.requireNonNullElse(eventEntity.getParticipantLimit(), 0));
        eventEntity.setRequestModeration(Objects.requireNonNullElse(eventEntity.getRequestModeration(), true));

        eventEntity = eventRepository.save(eventEntity);

        EventFullDto eventFullDto = fullEventMapper.toDto(eventEntity);
        eventFullDto.setViews(0);
        eventFullDto.setConfirmedRequests(0);

        log.info("New event has been created in database: {}", eventFullDto);

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventEntity.getInitiator().getId().equals(userId)) {
            throw new IllegalEventOperationException(String.format("User with id=%d is not initiator of event " +
                    "with id=%d and can't get full information about this event", userId, eventId));
        }

        eventFieldSetter.setConfirmedRequests(eventEntity);
        eventFieldSetter.setViews(eventEntity);

        EventFullDto eventDto = fullEventMapper.toDto(eventEntity);
        log.info("Event with id {} has been received from database: {}", eventId, eventDto);

        return eventDto;
    }

    @Override
    @Transactional
    public List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        Pageable pageable = new CustomPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));

        List<EventEntity> eventEntities = eventRepository.findByInitiator_Id(userId, pageable);

        eventFieldSetter.setConfirmedRequests(eventEntities);
        eventFieldSetter.setViews(eventEntities);

        List<EventShortDto> events = eventEntities.stream().map(shortEventMapper::toDto).collect(Collectors.toList());

        log.info("List of events received from database for user with id {}, {}", userId, events);

        return events;
    }

    @Override
    @Transactional
    public List<RequestDto> getRequestsForUserEvent(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity event = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new IllegalEventOperationException(String.format("User with id=%d can not get information " +
                    "about participation requests for event with id=%d. This user is not initiator of this " +
                    "event", userId, eventId));
        }

        List<RequestEntity> requestEntities = requestRepository.findByEvent(eventId);
        List<RequestDto> requestDtos = requestEntities.stream().map(requestMapper::toDto).collect(Collectors.toList());

        log.info("List of requests for event with id {} has been received from database: {}", eventId, requestDtos);
        return requestDtos;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventUserRequestDto dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventEntity.getInitiator().getId().equals(userId)) {
            throw new IllegalEventOperationException(String.format("User with id=%d can not update" +
                    "event with id=%d. This user is not initiator of this event", userId, eventId));
        }

        EventStatus eventStatus = eventEntity.getState();

        if (eventStatus == EventStatus.PUBLISHED) {
            throw new IllegalEventOperationException(String.format("Event with id=%d is in status %s and can't " +
                    "be updated", eventId, eventStatus));
        }

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventStartTimeException("Event should start at time, which is not earlier than " +
                    "two hours after current moment");
        }

        eventEntity.setTitle(Objects.requireNonNullElse(dto.getTitle(), eventEntity.getTitle()));
        eventEntity.setDescription(Objects.requireNonNullElse(dto.getDescription(), eventEntity.getDescription()));
        eventEntity.setAnnotation(Objects.requireNonNullElse(dto.getAnnotation(), eventEntity.getAnnotation()));
        eventEntity.setPaid(Objects.requireNonNullElse(dto.getPaid(), eventEntity.getPaid()));
        eventEntity.setParticipantLimit(Objects.requireNonNullElse(dto.getParticipantLimit(),
                eventEntity.getParticipantLimit()));
        eventEntity.setRequestModeration(Objects.requireNonNullElse(dto.getRequestModeration(),
                eventEntity.getRequestModeration()));
        eventEntity.setEventDate(Objects.requireNonNullElse(dto.getEventDate(), eventEntity.getEventDate()));

        if (dto.getCategory() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found",
                            dto.getCategory())));
            eventEntity.setCategory(category);
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW:
                    eventEntity.setState(EventStatus.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    eventEntity.setState(EventStatus.PENDING);
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
    public RequestStatusUpdateResultDto updateRequestStatus(Integer userId, Integer eventId, RequestStatusUpdateDto dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!eventEntity.getInitiator().getId().equals(userId)) {
            throw new IllegalEventOperationException(String.format("User with id=%d can not update" +
                    "participation requests for event with id=%d. " +
                    "This user is not initiator of this event", userId, eventId));
        }

        List<RequestEntity> requests = requestRepository.findAllById(dto.getRequestIds());

        long confirmedRequests = requestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);

        if (requests.size() != dto.getRequestIds().size()) {
            Set<Integer> foundedRequestsIds = requests.stream()
                    .map(RequestEntity::getId)
                    .collect(Collectors.toSet());
            dto.getRequestIds().removeAll(foundedRequestsIds);
            throw new NotFoundException(String.format("Participation requests with ids=%s was not found",
                    dto.getRequestIds()));
        }

        if ((eventEntity.getParticipantLimit() == 0 || !eventEntity.getRequestModeration())
                && dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            return new RequestStatusUpdateResultDto();
        }

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            long confirmedRequestsAfterRequestExecution = confirmedRequests + requests.size();
            if (confirmedRequestsAfterRequestExecution > eventEntity.getParticipantLimit()) {
                throw new IllegalRequestOperationException(String.format("For event with id=%d participant limit " +
                        "has been reached", eventId));
            }
        }

        for (RequestEntity requestEntity : requests) {
            if (requestEntity.getStatus() != RequestStatus.PENDING) {
                throw new IllegalRequestOperationException(String.format("Can not change status for request " +
                        "with id=%d. This request is not in PENDING status", requestEntity.getId()));
            }
        }

        RequestStatus newStatus = (dto.getStatus() == RequestUpdateStatus.CONFIRMED) ? RequestStatus.CONFIRMED :
                RequestStatus.REJECTED;

        requests.forEach(requestEntity -> requestEntity.setStatus(newStatus));

        List<RequestEntity> updatedRequests = requestRepository.saveAll(requests);

        List<RequestEntity> rejectedRequests = new ArrayList<>();

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED
                && confirmedRequests + requests.size() == eventEntity.getParticipantLimit()) {
            List<RequestEntity> pendingRequests = requestRepository.findByEventAndStatus(eventId, RequestStatus.PENDING);
            pendingRequests.forEach(requestEntity -> requestEntity.setStatus(RequestStatus.REJECTED));
            rejectedRequests = requestRepository.saveAll(pendingRequests);
        }
        List<RequestDto> updatedRequestsDto = updatedRequests.stream().map(requestMapper::toDto)
                .collect(Collectors.toList());

        List<RequestDto> rejectedRequestDto = rejectedRequests.stream().map(requestMapper::toDto)
                .collect(Collectors.toList());

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            RequestStatusUpdateResultDto resultDto = RequestStatusUpdateResultDto.builder()
                    .confirmedRequests(updatedRequestsDto)
                    .rejectedRequests(rejectedRequestDto).build();
            log.info("Requests statuses have been updated in database: {}", resultDto);
            return resultDto;
        } else {
            RequestStatusUpdateResultDto resultDto = RequestStatusUpdateResultDto.builder()
                    .rejectedRequests(updatedRequestsDto).build();
            log.info("Requests statuses have been updated in database: {}", resultDto);
            return resultDto;
        }
    }
}
