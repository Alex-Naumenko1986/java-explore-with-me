package ru.practicum.ewm.category.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.entity.CategoryEntity;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        CategoryEntity createdCategory = repository.save(mapper.toEntity(categoryDto));
        log.info("New category has been created in database: {}", createdCategory);
        return mapper.toDto(createdCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer categoryId) {
        repository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", categoryId)));
        repository.deleteById(categoryId);
        log.info("Category with id {} has been deleted from database", categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer categoryId, CategoryDto categoryDto) {
        repository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                String.format("Category with id=%d was not found", categoryId)));
        categoryDto.setId(categoryId);
        CategoryEntity updatedCategory = repository.save(mapper.toEntity(categoryDto));
        log.info("Category with id {} has been updated in database: {}", categoryId, updatedCategory);
        return mapper.toDto(updatedCategory);
    }
}
