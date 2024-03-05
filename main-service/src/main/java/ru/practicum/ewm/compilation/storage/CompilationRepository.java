package ru.practicum.ewm.compilation.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.compilation.entity.CompilationEntity;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Integer>,
        QuerydslPredicateExecutor<CompilationEntity> {
}
