package com.gestorpyme.service.mock;

import com.gestorpyme.dto.response.AuditLogPageResponse;
import com.gestorpyme.service.AuditService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Profile("mock")
public class NoOpAuditService implements AuditService {

    @Override
    public void log(String acao, String entidade, Long entidadeId, String detalhes) {
        // sem persistência no perfil mock
    }

    @Override
    public AuditLogPageResponse list(String entidade, LocalDateTime from, LocalDateTime to, int page, int size) {
        return new AuditLogPageResponse(List.of(), page, size, 0, 0);
    }
}
