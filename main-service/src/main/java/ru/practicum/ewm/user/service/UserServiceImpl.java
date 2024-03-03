package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.pageable.CustomPageable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.entity.UserEntity;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        UserEntity savedUser = userRepository.save(userMapper.toEntity(userDto));
        log.info("New user saved to database: {}", savedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers(Integer[] ids, Integer from, Integer size) {
        Pageable pageable = new CustomPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<UserEntity> userEntities;
        if (ids == null || ids.length == 0) {
            userEntities = userRepository.findAll(pageable).toList();
        } else {
            userEntities = userRepository.findByIdIn(List.of(ids), pageable);
        }
        log.info("List of users received from database: {}", userEntities);
        return userEntities.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id=%d was not found", userId)));
        userRepository.deleteById(userId);
        log.info("User with id {} has been deleted from database", userId);
    }
}
