package com.taskify.auth.utils;

import com.taskify.user.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    private UserDto user;

}