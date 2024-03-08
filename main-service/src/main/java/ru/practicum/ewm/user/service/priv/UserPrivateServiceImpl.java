package ru.practicum.ewm.user.service.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.entity.UserEntity;
import ru.practicum.ewm.user.exception.IllegalSubscriptionOperationException;
import ru.practicum.ewm.user.mapper.ShortUserMapper;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPrivateServiceImpl implements UserPrivateService {
    private final UserRepository userRepository;
    private final ShortUserMapper shortUserMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addSubscription(Integer userId, Integer subscribeOnId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        UserEntity subscribeOnUser = userRepository.findById(subscribeOnId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        if (userId.equals(subscribeOnId)) {
            throw new IllegalSubscriptionOperationException("The user can not subscribe to himself");
        }

        if (!subscribeOnUser.getSubscriptionAvailable()) {
            throw new IllegalSubscriptionOperationException(String.format("The user with id=%d has banned " +
                    "the subscription on himself", subscribeOnId));
        }

        if (user.getSubscribedOn().contains(subscribeOnId)) {
            throw new IllegalSubscriptionOperationException(String.format("The user with id=%d is already subscribed" +
                    " to user with id=%d", userId, subscribeOnId));
        }

        user.getSubscribedOn().add(subscribeOnId);
        user = userRepository.save(user);
        UserDto userDto = userMapper.toDto(user);

        userDto.setSubscribedOn(getSubscribedOn(user));

        log.info("Subscription has been created: {}", userDto);

        return userDto;
    }

    @Override
    @Transactional
    public UserDto cancelSubscription(Integer userId, Integer subscribedOnId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        userRepository.findById(subscribedOnId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        if (!user.getSubscribedOn().contains(subscribedOnId)) {
            throw new IllegalSubscriptionOperationException(String.format("The user with id=%d is not subscribed" +
                    " to user with id=%d", userId, subscribedOnId));
        }

        user.getSubscribedOn().remove(subscribedOnId);
        user = userRepository.save(user);
        UserDto userDto = userMapper.toDto(user);

        userDto.setSubscribedOn(getSubscribedOn(user));

        log.info("Subscription has been cancelled: {}", userDto);

        return userDto;
    }

    @Override
    @Transactional
    public UserDto updateSubscriptionAvailable(Integer userId, Boolean isAvailable) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        if (user.getSubscriptionAvailable().equals(isAvailable)) {
            throw new IllegalSubscriptionOperationException(String.format("Subscription availability for user " +
                    "with id=%d is already %s", userId, isAvailable));
        }

        user.setSubscriptionAvailable(isAvailable);
        user = userRepository.save(user);

        UserDto userDto = userMapper.toDto(user);

        userDto.setSubscribedOn(getSubscribedOn(user));

        log.info("Subscription availability has been changed: {}", userDto);

        return userDto;

    }

    @Override
    @Transactional
    public UserDto getUserById(Integer userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        UserDto userDto = userMapper.toDto(user);

        userDto.setSubscribedOn(getSubscribedOn(user));

        log.info("User has been received from database: {}", userDto);

        return userDto;
    }

    private List<UserShortDto> getSubscribedOn(UserEntity user) {
        List<UserEntity> subscribedOn = userRepository.findByIdIn(user.getSubscribedOn());
        return subscribedOn.stream().map(shortUserMapper::toDto)
                .sorted(Comparator.comparing(UserShortDto::getId)).collect(Collectors.toList());

    }
}
