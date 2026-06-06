package com.gestorpyme.controller;

import com.gestorpyme.dto.request.LoginRequest;
import com.gestorpyme.dto.response.LoginResponse;
import com.gestorpyme.dto.response.UserResponse;
import com.gestorpyme.security.CurrentUserProvider;
import com.gestorpyme.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserProvider currentUserProvider;

    public AuthController(AuthService authService, CurrentUserProvider currentUserProvider) {
        this.authService = authService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "Login (retorna JWT nos perfis dev/prod/test)")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Usuário autenticado")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(authService.me(currentUserProvider.getCurrentUserEmail()));
    }
}
