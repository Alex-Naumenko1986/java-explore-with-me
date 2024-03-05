package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.entity.CompilationEntity;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.mapper.ShortEventMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ShortEventMapper.class})
public interface CompilationMapper {
    List<EventShortDto> toEventShortDtoList(List<EventEntity> eventEntities);

    CompilationDto toDto(CompilationEntity compilationEntity);
}
