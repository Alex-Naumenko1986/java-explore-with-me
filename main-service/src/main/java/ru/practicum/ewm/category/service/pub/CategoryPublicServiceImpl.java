package ru.practicum.ewm.category.service.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.entity.CategoryEntity;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.pageable.CustomPageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    @Transactional
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable pageable = new CustomPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<CategoryEntity> categoryEntities = repository.findAll(pageable).toList();

        log.info("List of categories received from database: {}", categoryEntities);

        return categoryEntities.stream().map(mapper::toDto).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CategoryDto getCategoryById(Integer categoryId) {
        CategoryEntity categoryEntity = repository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%s was not found", categoryId)));

        log.info("Category with id {} has been received from database: {}", categoryId, categoryEntity);

        return mapper.toDto(categoryEntity);
    }
}
