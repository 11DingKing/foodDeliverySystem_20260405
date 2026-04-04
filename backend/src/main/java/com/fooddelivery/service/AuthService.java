package com.fooddelivery.service;

import com.fooddelivery.dto.LoginRequest;
import com.fooddelivery.dto.LoginResponse;
import com.fooddelivery.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
}
