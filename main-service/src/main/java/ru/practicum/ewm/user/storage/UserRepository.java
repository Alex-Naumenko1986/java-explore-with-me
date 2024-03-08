package ru.practicum.ewm.user.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.entity.UserEntity;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    List<UserEntity> findByIdIn(Collection<Integer> ids, Pageable pageable);

    List<UserEntity> findByIdIn(Collection<Integer> ids);

}
