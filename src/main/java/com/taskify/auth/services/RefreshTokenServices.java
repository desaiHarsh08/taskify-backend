package com.taskify.auth.services;

import com.taskify.auth.models.RefreshTokenModel;

public interface RefreshTokenServices {

    RefreshTokenModel createRefreshToken(String email);

    RefreshTokenModel verifyRefreshToken(String email);

}
