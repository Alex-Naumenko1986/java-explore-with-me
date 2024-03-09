package ru.practicum.ewm.event.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.entity.EventEntity;
import ru.practicum.ewm.event.enums.EventStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer>, QuerydslPredicateExecutor<EventEntity> {
    List<EventEntity> findByInitiator_IdInAndStateAndEventDateAfter(Collection<Integer> ids, EventStatus state, LocalDateTime eventDate, Pageable pageable);

    EventEntity findByIdAndState(Integer id, EventStatus state);

    List<EventEntity> findByIdIn(Collection<Integer> ids);

    List<EventEntity> findByInitiator_Id(Integer id, Pageable pageable);
}
