package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "subscribedOn", ignore = true)
    UserEntity toEntity(UserDto userDto);

    @Mapping(target = "subscribedOn", ignore = true)
    UserDto toDto(UserEntity userEntity);
}
