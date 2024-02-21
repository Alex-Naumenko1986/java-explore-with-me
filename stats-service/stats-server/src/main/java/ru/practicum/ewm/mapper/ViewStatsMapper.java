package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.entity.ViewStatsEntity;

@Component
public class ViewStatsMapper {
    public ViewStatsDto toDto(ViewStatsEntity viewStatsEntity) {
        return new ViewStatsDto(viewStatsEntity.getApp(), viewStatsEntity.getUri(), viewStatsEntity.getHits().intValue());
    }
}
