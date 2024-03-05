package ru.practicum.ewm.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.entity.RequestEntity;
import ru.practicum.ewm.request.enums.RequestStatus;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<RequestEntity, Integer> {
    long countByRequesterAndEvent(Integer requester, Integer event);

    long countByEventAndStatus(Integer event, RequestStatus status);

    List<RequestEntity> findByRequester(Integer requester);

    List<RequestEntity> findByEventInAndStatus(Collection<Integer> events, RequestStatus status);

    List<RequestEntity> findByEvent(Integer event);

    List<RequestEntity> findByEventAndStatus(Integer event, RequestStatus status);


}
