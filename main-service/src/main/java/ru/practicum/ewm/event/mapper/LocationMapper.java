package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.entity.LocationEntity;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationEntity toEntity(LocationDto locationDto);
}
