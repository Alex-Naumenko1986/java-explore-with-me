package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.entity.CategoryEntity;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.entity.EventEntity;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface NewEventMapper {
    EventEntity toEntity(NewEventDto eventDto);

    default CategoryEntity map(Integer value) {
        return CategoryEntity.builder().id(value).build();
    }
}
