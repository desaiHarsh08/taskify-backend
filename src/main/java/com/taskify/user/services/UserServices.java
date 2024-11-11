package com.taskify.user.services;

import com.taskify.common.utils.PageResponse;
import com.taskify.user.dtos.UserDto;

import java.util.List;

public interface UserServices {

    UserDto createUser(UserDto userDto);

    PageResponse<UserDto> getAllUsers(int pageNumber, Integer pageSize);

    UserDto getUserById(Long id);

    UserDto getUserByEmail(String email);

    UserDto updateUser(UserDto userDto);

    boolean resetPassword(Long id, String rawPassword);

    List<UserDto> getUsersByDepartment(String department);

}
