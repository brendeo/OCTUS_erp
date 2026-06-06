package com.gestorpyme.dto.response;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String acao,
        String entidade,
        Long entidadeId,
        String usuario,
        String detalhes,
        LocalDateTime createdAt
) {}
