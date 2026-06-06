package com.gestorpyme.dto.response;

import com.gestorpyme.domain.enums.UserRole;

public record UserResponse(
        Long id,
        String nome,
        String email,
        UserRole perfil
) {}
