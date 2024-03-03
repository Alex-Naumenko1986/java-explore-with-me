package ru.practicum.ewm.category.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.admin.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryAdminController {
    private final CategoryAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        CategoryDto createdCategory = service.createCategory(categoryDto);
        log.info("New category has been created: {}", categoryDto);
        return createdCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Min(1) Integer catId) {
        service.deleteCategory(catId);
        log.info("Category with id {} had been removed", catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto,
                                      @PathVariable @Min(1) Integer catId) {
        CategoryDto updatedCategory = service.updateCategory(catId, categoryDto);
        log.info("Category with id {} has been updated: {}", catId, categoryDto);
        return updatedCategory;
    }
}
