package ru.practicum.ewm.user.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.pageable.CustomPageable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.entity.UserEntity;
import ru.practicum.ewm.user.mapper.ShortUserMapper;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminServiceImpl implements UserAdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ShortUserMapper shortUserMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity.setSubscriptionAvailable(Objects.requireNonNullElse(userEntity.getSubscriptionAvailable(),
                true));
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("New user saved to database: {}", savedUser);
        UserDto createdUserDto = userMapper.toDto(savedUser);
        createdUserDto.setSubscribedOn(new ArrayList<>());
        return createdUserDto;
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

        Set<Integer> subscribedOn = new HashSet<>();
        userEntities.forEach(userEntity -> subscribedOn.addAll(userEntity.getSubscribedOn()));

        List<UserEntity> subscribedOnUserEntities = userRepository.findByIdIn(subscribedOn);
        List<UserDto> result = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            List<UserShortDto> subscriptions = subscribedOnUserEntities.stream()
                    .filter(userEntity1 -> userEntity.getSubscribedOn().contains(userEntity1.getId()))
                    .map(shortUserMapper::toDto)
                    .sorted(Comparator.comparing(UserShortDto::getId)).collect(Collectors.toList());

            UserDto userDto = userMapper.toDto(userEntity);
            userDto.setSubscribedOn(subscriptions);
            result.add(userDto);
        }

        log.info("List of users has been received from database: {}", result);
        return result;
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
