package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.enums.EventStatus;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.entity.RequestEntity;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.exception.IllegalRequestOperationException;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public RequestDto createRequest(Integer userId, Integer eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        EventEntity event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        long count = requestRepository.countByRequesterAndEvent(userId, eventId);
        if (count != 0) {
            throw new IllegalRequestOperationException("You can not add repeated request");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new IllegalRequestOperationException("The initiator of event can not add participation request");
        }

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new IllegalRequestOperationException("The participation request can not be added to unpublished event");
        }

        long confirmedRequests = requestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        if (confirmedRequests >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new IllegalRequestOperationException(String.format("Participation request limit has been" +
                    " reached for event with id %d", eventId));
        }

        RequestStatus status = (event.getRequestModeration() && event.getParticipantLimit() != 0) ?
                RequestStatus.PENDING : RequestStatus.CONFIRMED;

        RequestEntity requestEntity = RequestEntity.builder().event(eventId).requester(userId).status(status)
                .created(LocalDateTime.now()).build();

        requestEntity = requestRepository.save(requestEntity);
        log.info("New participant request has been saved to database: {}", requestEntity);

        return requestMapper.toDto(requestEntity);
    }

    @Override
    public List<RequestDto> getUserRequests(Integer userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        List<RequestEntity> requestEntities = requestRepository.findByRequester(userId);
        log.info("List of participation requests has been received from database: {}", requestEntities);
        return requestEntities.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public RequestDto cancelRequest(Integer userId, Integer requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d was not found", userId)));

        RequestEntity requestEntity = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Participation request with id %d was not found", requestId)));

        if (!requestEntity.getRequester().equals(userId)) {
            throw new IllegalRequestOperationException(String.format("User with id %d has not created participation " +
                    "request with id %d and can't cancel it", userId, requestId));
        }

        requestEntity.setStatus(RequestStatus.CANCELED);
        requestEntity = requestRepository.save(requestEntity);

        log.info("Participation request with id {} has been cancelled", requestId);

        return requestMapper.toDto(requestEntity);
    }
}
