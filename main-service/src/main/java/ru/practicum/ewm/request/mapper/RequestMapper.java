package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.entity.RequestEntity;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDto toDto(RequestEntity requestEntity);
}
