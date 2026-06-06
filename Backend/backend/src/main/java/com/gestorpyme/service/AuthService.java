package com.gestorpyme.service;

import com.gestorpyme.dto.request.LoginRequest;
import com.gestorpyme.dto.response.LoginResponse;
import com.gestorpyme.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UserResponse me(String email);
}
