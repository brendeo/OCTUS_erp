package com.gestorpyme.service.jpa;

import com.gestorpyme.domain.entity.PayableEntity;
import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.dto.request.CreatePayableRequest;
import com.gestorpyme.dto.request.MarkPaidRequest;
import com.gestorpyme.dto.request.UpdatePayableRequest;
import com.gestorpyme.dto.response.PayableResponse;
import com.gestorpyme.exception.BusinessException;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.repository.PayableRepository;
import com.gestorpyme.service.AuditService;
import com.gestorpyme.service.PayableService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Profile({"dev", "prod", "test"})
public class JpaPayableService implements PayableService {

    private final PayableRepository repository;
    private final AuditService auditService;

    public JpaPayableService(PayableRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Override
    public List<PayableResponse> list(PayableStatus status, LocalDate from, LocalDate to) {
        return repository.findAll().stream()
                .filter(p -> from == null || !p.getVencimento().isBefore(from))
                .filter(p -> to == null || !p.getVencimento().isAfter(to))
                .map(EntityMapper::toPayableResponse)
                .filter(p -> status == null || p.status() == status)
                .sorted(Comparator.comparing(PayableResponse::vencimento))
                .toList();
    }

    @Override
    public PayableResponse getById(Long id) {
        return EntityMapper.toPayableResponse(find(id));
    }

    @Override
    @Transactional
    public PayableResponse create(CreatePayableRequest request) {
        PayableEntity e = new PayableEntity();
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        e.setStatus(PayableStatus.PENDENTE);
        PayableEntity saved = repository.save(e);
        auditService.log("CREATE", "Payable", saved.getId(), saved.getDescricao());
        return EntityMapper.toPayableResponse(saved);
    }

    @Override
    @Transactional
    public PayableResponse update(Long id, UpdatePayableRequest request) {
        PayableEntity e = find(id);
        if (e.getStatus() == PayableStatus.PAGA) {
            throw new BusinessException("Conta já paga não pode ser editada");
        }
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        PayableEntity saved = repository.save(e);
        auditService.log("UPDATE", "Payable", saved.getId(), saved.getDescricao());
        return EntityMapper.toPayableResponse(saved);
    }

    @Override
    @Transactional
    public PayableResponse markPaid(Long id, MarkPaidRequest request) {
        PayableEntity e = find(id);
        e.setStatus(PayableStatus.PAGA);
        e.setDataPagamento(request != null && request.dataPagamento() != null
                ? request.dataPagamento() : LocalDate.now());
        PayableEntity saved = repository.save(e);
        auditService.log("MARK_PAID", "Payable", saved.getId(), saved.getDescricao());
        return EntityMapper.toPayableResponse(saved);
    }

    private PayableEntity find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a pagar não encontrada: " + id));
    }
}
