package ru.practicum.ewm.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Creating new user {}", userDto);
        UserDto createdUserDto = userAdminService.createUser(userDto);
        log.info("Created new user: {}", createdUserDto);
        return createdUserDto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NotNull @Min(1) Integer userId) {
        log.info("Deleting user with id {}", userId);
        userAdminService.deleteUser(userId);
        log.info("User with id {} has been deleted", userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Integer[] ids,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Getting users with parameters: ids {}, from {}, size {}", ids, from, size);
        return userAdminService.getUsers(ids, from, size);
    }
}
