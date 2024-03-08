package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.entity.LocationEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {
    LocationEntity toEntity(LocationDto locationDto);
}
