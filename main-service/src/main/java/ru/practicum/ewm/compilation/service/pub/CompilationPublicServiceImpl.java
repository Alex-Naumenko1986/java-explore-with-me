package ru.practicum.ewm.compilation.service.pub;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.entity.CompilationEntity;
import ru.practicum.ewm.compilation.entity.QCompilationEntity;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.utils.EventFieldSetter;
import ru.practicum.ewm.pageable.CustomPageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final EventFieldSetter eventFieldSetter;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto getCompilationById(Integer compId) {
        CompilationEntity compilationEntity = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%d was not found", compId)));

        List<EventEntity> events = compilationEntity.getEvents();

        eventFieldSetter.setConfirmedRequests(events);
        eventFieldSetter.setViews(events);

        CompilationDto compilationDto = compilationMapper.toDto(compilationEntity);

        log.info("Compilation has been received from database: {}", compilationDto);

        return compilationDto;
    }

    @Override
    public List<CompilationDto> searchCompilations(Boolean pinned, Integer from, Integer size) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(Expressions.asBoolean(true).isTrue());

        if (pinned != null) {
            predicates.add(QCompilationEntity.compilationEntity.pinned.eq(pinned));
        }

        Predicate predicate = ExpressionUtils.allOf(predicates);

        Pageable pageable = new CustomPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));

        List<CompilationEntity> compilationEntities = compilationRepository.findAll(predicate, pageable).toList();

        List<EventEntity> events = new ArrayList<>();

        compilationEntities.forEach(compilationEntity -> events.addAll(compilationEntity.getEvents()));

        eventFieldSetter.setViews(events);
        eventFieldSetter.setConfirmedRequests(events);

        List<CompilationDto> compilationDtos = compilationEntities.stream().map(compilationMapper::toDto)
                .collect(Collectors.toList());

        log.info("List of compilations has been received from database: {}", compilationDtos);

        return compilationDtos;
    }
}
