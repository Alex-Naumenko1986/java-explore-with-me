package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(CategoryEntity categoryEntity);

    CategoryEntity toEntity(CategoryDto categoryDto);

}
