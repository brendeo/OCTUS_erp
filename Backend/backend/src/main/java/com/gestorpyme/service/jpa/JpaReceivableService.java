package com.gestorpyme.service.jpa;

import com.gestorpyme.domain.entity.ReceivableEntity;
import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.request.CreateReceivableRequest;
import com.gestorpyme.dto.request.MarkReceivedRequest;
import com.gestorpyme.dto.request.UpdateReceivableRequest;
import com.gestorpyme.dto.response.ReceivableResponse;
import com.gestorpyme.exception.BusinessException;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.repository.ReceivableRepository;
import com.gestorpyme.service.AuditService;
import com.gestorpyme.service.ReceivableService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Profile({"dev", "prod", "test"})
public class JpaReceivableService implements ReceivableService {

    private final ReceivableRepository repository;
    private final AuditService auditService;

    public JpaReceivableService(ReceivableRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Override
    public List<ReceivableResponse> list(ReceivableStatus status, LocalDate from, LocalDate to) {
        return repository.findAll().stream()
                .filter(r -> from == null || !r.getVencimento().isBefore(from))
                .filter(r -> to == null || !r.getVencimento().isAfter(to))
                .map(EntityMapper::toReceivableResponse)
                .filter(r -> status == null || r.status() == status)
                .sorted(Comparator.comparing(ReceivableResponse::vencimento))
                .toList();
    }

    @Override
    public ReceivableResponse getById(Long id) {
        return EntityMapper.toReceivableResponse(find(id));
    }

    @Override
    @Transactional
    public ReceivableResponse create(CreateReceivableRequest request) {
        ReceivableEntity e = new ReceivableEntity();
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        e.setStatus(ReceivableStatus.PENDENTE);
        ReceivableEntity saved = repository.save(e);
        auditService.log("CREATE", "Receivable", saved.getId(), saved.getDescricao());
        return EntityMapper.toReceivableResponse(saved);
    }

    @Override
    @Transactional
    public ReceivableResponse update(Long id, UpdateReceivableRequest request) {
        ReceivableEntity e = find(id);
        if (e.getStatus() == ReceivableStatus.RECEBIDA) {
            throw new BusinessException("Conta já recebida não pode ser editada");
        }
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        ReceivableEntity saved = repository.save(e);
        auditService.log("UPDATE", "Receivable", saved.getId(), saved.getDescricao());
        return EntityMapper.toReceivableResponse(saved);
    }

    @Override
    @Transactional
    public ReceivableResponse markReceived(Long id, MarkReceivedRequest request) {
        ReceivableEntity e = find(id);
        e.setStatus(ReceivableStatus.RECEBIDA);
        e.setDataRecebimento(request != null && request.dataRecebimento() != null
                ? request.dataRecebimento() : LocalDate.now());
        ReceivableEntity saved = repository.save(e);
        auditService.log("MARK_RECEIVED", "Receivable", saved.getId(), saved.getDescricao());
        return EntityMapper.toReceivableResponse(saved);
    }

    private ReceivableEntity find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a receber não encontrada: " + id));
    }
}
