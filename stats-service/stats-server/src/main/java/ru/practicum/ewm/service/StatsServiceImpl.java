package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.EndpointHitEntity;
import ru.practicum.ewm.entity.ViewStatsEntity;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;
    private final StatsRepository repository;

    @Override
    public void createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHitEntity entity = repository.save(endpointHitMapper.toEntity(endpointHitDto));
        log.info("Endpoint hit: {} was saved to database", entity);
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<ViewStatsEntity> viewStatsEntities = new ArrayList<>();

        if (unique && uris.length == 0) {
            viewStatsEntities = repository.getStatsForAllEndpointHitsWithUniqueIp(start, end);
        } else if (unique) {
            viewStatsEntities = repository.getStatsForEndpointHitsWithUniqueIp(start, end, uris);
        } else if (uris.length == 0) {
            viewStatsEntities = repository.getStatsForAllEndpointHitsWithNonUniqueIp(start, end);
        } else {
            viewStatsEntities = repository.getStatsForEndpointHitsWithNonUniqueIp(start, end, uris);
        }

        log.info("Received statistics from database: {}", viewStatsEntities);

        return viewStatsEntities.stream().map(viewStatsMapper::toDto).collect(Collectors.toList());
    }
}
