package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.user.mapper.ShortUserMapper;

@Mapper(componentModel = "spring", uses = {ShortUserMapper.class, CategoryMapper.class})
public interface ShortEventMapper {
    EventShortDto toDto(EventEntity eventEntity);
}
