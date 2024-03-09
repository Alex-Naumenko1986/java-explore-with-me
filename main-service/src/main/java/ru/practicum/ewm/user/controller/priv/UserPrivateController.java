package ru.practicum.ewm.user.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.priv.UserPrivateService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserPrivateController {
    private final UserPrivateService service;

    @PatchMapping("/subscriptions/{otherUserId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto addSubscription(@PathVariable @NotNull @Min(1) Integer userId,
                                   @PathVariable(name = "otherUserId") @NotNull @Min(1) Integer subscribeOnId) {
        log.info("Adding subscription from user with id {} to user with id {}", userId, subscribeOnId);
        UserDto userDto = service.addSubscription(userId, subscribeOnId);
        log.info("Subscription has been added for user: {}", userDto);
        return userDto;
    }

    @PatchMapping("/subscriptions/{otherUserId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public UserDto cancelSubscription(@PathVariable @NotNull @Min(1) Integer userId,
                                      @PathVariable(name = "otherUserId") @NotNull @Min(1) Integer subscribedOnId) {
        log.info("Cancelling subscription from user with id {} to user with id {}", userId, subscribedOnId);
        UserDto userDto = service.cancelSubscription(userId, subscribedOnId);
        log.info("Subscription has been cancelled for user: {}", userDto);
        return userDto;
    }

    @PatchMapping("/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateSubscriptionAvailable(@PathVariable @NotNull @Min(1) Integer userId,
                                               @RequestParam(name = "available") @NotNull Boolean isAvailable) {
        log.info("Updating subscription available to {} for user with id {}", isAvailable, userId);
        UserDto userDto = service.updateSubscriptionAvailable(userId, isAvailable);
        log.info("Subscription available is changed for user: {}", userDto);
        return userDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable @NotNull @Min(1) Integer userId) {
        log.info("Getting user with id: {}", userId);
        UserDto userDto = service.getUserById(userId);
        log.info("Received user: {}", userDto);
        return userDto;
    }
}
