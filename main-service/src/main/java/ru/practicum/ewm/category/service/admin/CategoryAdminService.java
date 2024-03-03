package ru.practicum.ewm.category.service.admin;

import ru.practicum.ewm.category.dto.CategoryDto;

public interface CategoryAdminService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Integer categoryId);

    CategoryDto updateCategory(Integer categoryId, CategoryDto categoryDto);
}
