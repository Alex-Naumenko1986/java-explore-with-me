package ru.practicum.ewm.compilation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.admin.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {
    private final CompilationAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto dto) {
        CompilationDto createdCompilation = service.createCompilation(dto);
        log.info("New compilation has been created: {}", createdCompilation);
        return createdCompilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Min(1) Integer compId) {
        service.deleteCompilation(compId);
        log.info("Compilation with id {} has been deleted", compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable @Min(1) Integer compId,
                                            @RequestBody @Valid UpdateCompilationDto dto) {
        CompilationDto updatedCompilation = service.updateCompilation(compId, dto);
        log.info("Compilation has been updated: {}", updatedCompilation);
        return updatedCompilation;
    }
}
