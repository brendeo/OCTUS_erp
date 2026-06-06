package com.gestorpyme.service.jpa;

import com.gestorpyme.domain.entity.AuditLogEntity;
import com.gestorpyme.dto.response.AuditLogPageResponse;
import com.gestorpyme.dto.response.AuditLogResponse;
import com.gestorpyme.repository.AuditLogRepository;
import com.gestorpyme.security.CurrentUserProvider;
import com.gestorpyme.service.AuditService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Profile({"dev", "prod", "test"})
public class JpaAuditService implements AuditService {

    private final AuditLogRepository repository;
    private final CurrentUserProvider currentUserProvider;

    public JpaAuditService(AuditLogRepository repository, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional
    public void log(String acao, String entidade, Long entidadeId, String detalhes) {
        AuditLogEntity log = new AuditLogEntity();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setUsuario(currentUserProvider.getCreatedByLabel());
        log.setDetalhes(detalhes);
        repository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogPageResponse list(String entidade, LocalDateTime from, LocalDateTime to, int page, int size) {
        int pageSize = size > 0 ? size : 20;
        Page<AuditLogEntity> result = repository.findFiltered(
                blankToNull(entidade), from, to, PageRequest.of(page, pageSize));
        List<AuditLogResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();
        return new AuditLogPageResponse(
                content, page, pageSize, result.getTotalElements(), result.getTotalPages());
    }

    private AuditLogResponse toResponse(AuditLogEntity e) {
        return new AuditLogResponse(
                e.getId(), e.getAcao(), e.getEntidade(), e.getEntidadeId(),
                e.getUsuario(), e.getDetalhes(), e.getCreatedAt());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
