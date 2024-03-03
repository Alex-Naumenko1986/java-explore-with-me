package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import ru.practicum.ewm.constant.Constants;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.EndpointHitEntity;
import ru.practicum.ewm.entity.ViewStatsEntity;
import ru.practicum.ewm.exception.InvalidDateRangeException;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.storage.StatsRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    @Override
    public void createEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHitEntity entity = repository.save(endpointHitMapper.toEntity(endpointHitDto));
        log.info("Endpoint hit: {} was saved to database", entity);
    }

    @Override
    public List<ViewStatsDto> getStatistics(String start, String end, String[] uris, boolean unique) {
        LocalDateTime startDate = decodeDate(start);
        LocalDateTime endDate = decodeDate(end);

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("End date should be after start date");
        }

        List<ViewStatsEntity> viewStatsEntities = new ArrayList<>();

        if (unique && uris.length == 0) {
            viewStatsEntities = repository.getStatsForAllEndpointHitsWithUniqueIp(startDate, endDate);
        } else if (unique) {
            viewStatsEntities = repository.getStatsForEndpointHitsWithUniqueIp(startDate, endDate, uris);
        } else if (uris.length == 0) {
            viewStatsEntities = repository.getStatsForAllEndpointHitsWithNonUniqueIp(startDate, endDate);
        } else {
            viewStatsEntities = repository.getStatsForEndpointHitsWithNonUniqueIp(startDate, endDate, uris);
        }

        log.info("Received statistics from database: {}", viewStatsEntities);

        return viewStatsEntities.stream().map(viewStatsMapper::toDto).collect(Collectors.toList());
    }

    private LocalDateTime decodeDate(String date) {
        String dateEncoded = UriUtils.decode(date, StandardCharsets.UTF_8);
        return LocalDateTime.parse(dateEncoded, formatter);
    }
}
