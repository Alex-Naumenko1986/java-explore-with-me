package ru.practicum.ewm.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        UserDto createdUserDto = userService.createUser(userDto);
        log.info("Created new user: {}", createdUserDto);
        return createdUserDto;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Min(1) Integer userId) {
        userService.deleteUser(userId);
        log.info("User with id {} has been deleted", userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Integer[] ids,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Getting users with parameters: ids {}, from {}, size {}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }
}
