package com.gestorpyme.dto.response;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEm,
        UserResponse usuario
) {}
