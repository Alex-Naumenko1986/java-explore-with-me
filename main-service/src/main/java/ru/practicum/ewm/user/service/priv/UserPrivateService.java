package ru.practicum.ewm.user.service.priv;

import ru.practicum.ewm.user.dto.UserDto;

public interface UserPrivateService {
    UserDto addSubscription(Integer userId, Integer subscribeOnId);

    UserDto cancelSubscription(Integer userId, Integer subscribedOnId);

    UserDto updateSubscriptionAvailable(Integer userId, Boolean isAvailable);

    UserDto getUserById(Integer userId);
}
