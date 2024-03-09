package ru.practicum.ewm.user.service.admin;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserAdminService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(Integer[] ids, Integer from, Integer size);

    void deleteUser(Integer userId);
}
