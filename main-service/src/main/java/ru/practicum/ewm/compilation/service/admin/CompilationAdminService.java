package ru.practicum.ewm.compilation.service.admin;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

public interface CompilationAdminService {
    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilation(Integer compId);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationDto dto);
}
