package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.user.mapper.ShortUserMapper;

@Mapper(componentModel = "spring", uses = {ShortUserMapper.class, LocationMapper.class, CategoryMapper.class})
public interface FullEventMapper {
    EventFullDto toDto(EventEntity eventEntity);
}
