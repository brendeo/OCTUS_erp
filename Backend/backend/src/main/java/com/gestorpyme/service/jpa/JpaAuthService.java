package com.gestorpyme.service.jpa;

import com.gestorpyme.domain.entity.UserEntity;
import com.gestorpyme.dto.request.LoginRequest;
import com.gestorpyme.dto.response.LoginResponse;
import com.gestorpyme.dto.response.UserResponse;
import com.gestorpyme.exception.BusinessException;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.repository.UserRepository;
import com.gestorpyme.security.JwtService;
import com.gestorpyme.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "prod", "test"})
public class JpaAuthService implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationMs;

    public JpaAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                          @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas"));
        if (!passwordEncoder.matches(request.senha(), user.getSenhaHash())) {
            throw new BusinessException("Credenciais inválidas");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getPerfil().name());
        return new LoginResponse(token, "Bearer", expirationMs, EntityMapper.toUserResponse(user));
    }

    @Override
    public UserResponse me(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return EntityMapper.toUserResponse(user);
    }
}
