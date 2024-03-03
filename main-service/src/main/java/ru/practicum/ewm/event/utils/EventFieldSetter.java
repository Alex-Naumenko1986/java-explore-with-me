package ru.practicum.ewm.event.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.request.entity.RequestEntity;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventFieldSetter {
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public void setViews(EventEntity eventEntity) {
        if (eventEntity.getPublishedOn() == null) {
            eventEntity.setViews(0);
            return;
        }
        LocalDateTime start = eventEntity.getPublishedOn();
        LocalDateTime end = LocalDateTime.now();
        Integer eventId = eventEntity.getId();
        String[] uris = new String[]{"/events/" + eventId};
        List<ViewStatsDto> viewStatsDto = statsClient.getStatistics(start, end, uris, true);
        if (viewStatsDto.size() == 0) {
            eventEntity.setViews(0);
            return;
        }
        eventEntity.setViews(viewStatsDto.get(0).getHits());
    }

    public void setConfirmedRequests(EventEntity eventEntity) {
        long confirmedRequests = requestRepository.countByEventAndStatus(eventEntity.getId(), RequestStatus.CONFIRMED);
        eventEntity.setConfirmedRequests((int) confirmedRequests);
    }

    public void setConfirmedRequests(List<EventEntity> eventEntities) {
        List<Integer> eventIds = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toList());
        List<RequestEntity> requests = requestRepository
                .findByEventInAndStatus(eventIds, RequestStatus.CONFIRMED);
        for (EventEntity eventEntity : eventEntities) {
            long confirmedRequests = requests.stream()
                    .filter(requestEntity -> requestEntity.getEvent().equals(eventEntity.getId()))
                    .count();
            eventEntity.setConfirmedRequests((int) confirmedRequests);
        }
    }

    public void setViews(List<EventEntity> eventEntities) {
        Optional<EventEntity> eventWithMinPublishingDate = eventEntities.stream()
                .filter(eventEntity -> eventEntity.getPublishedOn() != null)
                .min(Comparator.comparing(EventEntity::getPublishedOn));
        if (eventWithMinPublishingDate.isEmpty()) {
            for (EventEntity eventEntity : eventEntities) {
                eventEntity.setViews(0);
            }
            return;
        }
        LocalDateTime start = eventWithMinPublishingDate.get().getPublishedOn();
        LocalDateTime end = LocalDateTime.now();
        List<String> urisAsList = new ArrayList<>();

        for (EventEntity eventEntity : eventEntities) {
            urisAsList.add("/events/" + eventEntity.getId());
        }

        String[] array = new String[urisAsList.size()];

        List<ViewStatsDto> viewStatsDtos = statsClient.getStatistics(start, end, urisAsList.toArray(array), true);

        for (EventEntity eventEntity : eventEntities) {
            Optional<ViewStatsDto> optionalViewStatsDto = viewStatsDtos.stream()
                    .filter(viewStatsDto -> viewStatsDto.getUri().equals("/events/" + eventEntity.getId()))
                    .findFirst();

            if (optionalViewStatsDto.isPresent()) {
                eventEntity.setViews(optionalViewStatsDto.get().getHits());
            } else {
                eventEntity.setViews(0);
            }
        }
    }
}
