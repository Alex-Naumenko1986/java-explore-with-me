package ru.practicum.ewm.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.entity.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {
}
