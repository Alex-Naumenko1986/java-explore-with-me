package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.entity.EndpointHitEntity;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHitEntity toEntity(EndpointHitDto endpointHitDto);
}
