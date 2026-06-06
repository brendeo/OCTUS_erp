package com.gestorpyme.service;

import com.gestorpyme.dto.response.AuditLogPageResponse;

import java.time.LocalDateTime;

public interface AuditService {
    void log(String acao, String entidade, Long entidadeId, String detalhes);

    AuditLogPageResponse list(String entidade, LocalDateTime from, LocalDateTime to, int page, int size);
}
