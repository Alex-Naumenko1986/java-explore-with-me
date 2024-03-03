package ru.practicum.ewm.compilation.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.entity.CompilationEntity;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.event.utils.EventFieldSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventFieldSetter eventFieldSetter;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        CompilationEntity compilationEntity = new CompilationEntity();
        compilationEntity.setTitle(dto.getTitle());
        compilationEntity.setPinned(Objects.requireNonNullElse(dto.getPinned(), false));

        if (dto.getEvents() == null) {
            compilationEntity.setEvents(new ArrayList<>());
        } else {
            List<EventEntity> eventEntities = eventRepository.findByIdIn(dto.getEvents());
            if (eventEntities.size() != dto.getEvents().size()) {
                Set<Integer> foundedEvents = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toSet());
                dto.getEvents().removeAll(foundedEvents);
                throw new NotFoundException(String.format("Events with ids=%s were not found", dto.getEvents()));
            }
            eventFieldSetter.setViews(eventEntities);
            eventFieldSetter.setConfirmedRequests(eventEntities);
            compilationEntity.setEvents(eventEntities);
        }
        compilationEntity = compilationRepository.save(compilationEntity);
        CompilationDto compilationDto = compilationMapper.toDto(compilationEntity);

        log.info("New compilation has been created: {}", compilationDto);

        return compilationDto;
    }

    @Override
    public void deleteCompilation(Integer compId) {
        compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        compilationRepository.deleteById(compId);

        log.info("Compilation with id {} has been deleted from database", compId);
    }

    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationDto dto) {
        CompilationEntity compilationEntity = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        compilationEntity.setTitle(Objects.requireNonNullElse(dto.getTitle(), compilationEntity.getTitle()));
        compilationEntity.setPinned(Objects.requireNonNullElse(dto.getPinned(), compilationEntity.getPinned()));

        if (dto.getEvents() != null && dto.getEvents().size() == 0) {
            compilationEntity.setEvents(new ArrayList<>());
        } else if (dto.getEvents() != null) {
            List<EventEntity> eventEntities = eventRepository.findByIdIn(dto.getEvents());
            if (eventEntities.size() != dto.getEvents().size()) {
                Set<Integer> foundedEvents = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toSet());
                dto.getEvents().removeAll(foundedEvents);
                throw new NotFoundException(String.format("Events with ids=%s were not found", dto.getEvents()));
            }
            eventFieldSetter.setViews(eventEntities);
            eventFieldSetter.setConfirmedRequests(eventEntities);
            compilationEntity.setEvents(eventEntities);
        }
        compilationEntity = compilationRepository.save(compilationEntity);
        CompilationDto compilationDto = compilationMapper.toDto(compilationEntity);

        log.info("Compilation has been updated in database: {}", compilationDto);

        return compilationDto;
    }
}
