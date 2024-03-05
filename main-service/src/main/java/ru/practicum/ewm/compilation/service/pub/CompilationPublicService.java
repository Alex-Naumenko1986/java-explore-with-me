package ru.practicum.ewm.compilation.service.pub;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    CompilationDto getCompilationById(Integer compId);

    List<CompilationDto> searchCompilations(Boolean pinned, Integer from, Integer size);
}
