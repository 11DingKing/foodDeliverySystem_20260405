package com.fooddelivery.service;

import com.fooddelivery.dto.PasswordUpdateRequest;
import com.fooddelivery.dto.UserUpdateRequest;
import com.fooddelivery.entity.User;

public interface UserService {
    User getCurrentUser();
    void updateProfile(UserUpdateRequest request);
    void updatePassword(PasswordUpdateRequest request);
    void updateAvatar(String avatarUrl);
}
