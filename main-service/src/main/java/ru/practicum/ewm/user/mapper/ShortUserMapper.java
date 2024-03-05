package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface ShortUserMapper {
    UserShortDto toDto(UserEntity userEntity);
}
