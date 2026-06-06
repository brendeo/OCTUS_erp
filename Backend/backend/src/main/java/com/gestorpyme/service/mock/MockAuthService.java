package com.gestorpyme.service.mock;

import com.gestorpyme.domain.entity.UserEntity;
import com.gestorpyme.dto.request.LoginRequest;
import com.gestorpyme.dto.response.LoginResponse;
import com.gestorpyme.dto.response.UserResponse;
import com.gestorpyme.exception.BusinessException;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.mock.MockDataStore;
import com.gestorpyme.security.JwtService;
import com.gestorpyme.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockAuthService implements AuthService {

    private final MockDataStore store;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationMs;

    public MockAuthService(MockDataStore store, PasswordEncoder passwordEncoder, JwtService jwtService,
                           @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.store = store;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserEntity user = store.getUsers().values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(request.email()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));
        if (!passwordEncoder.matches(request.senha(), user.getSenhaHash())) {
            throw new BusinessException("Credenciais inválidas");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getPerfil().name());
        return new LoginResponse(token, "Bearer", expirationMs, EntityMapper.toUserResponse(user));
    }

    @Override
    public UserResponse me(String email) {
        UserEntity user = store.getUsers().values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return EntityMapper.toUserResponse(user);
    }
}
