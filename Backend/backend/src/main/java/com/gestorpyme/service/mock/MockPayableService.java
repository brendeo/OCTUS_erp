package com.gestorpyme.service.mock;

import com.gestorpyme.domain.entity.PayableEntity;
import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.dto.request.CreatePayableRequest;
import com.gestorpyme.dto.request.MarkPaidRequest;
import com.gestorpyme.dto.request.UpdatePayableRequest;
import com.gestorpyme.dto.response.PayableResponse;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.mock.MockDataStore;
import com.gestorpyme.service.PayableService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Profile("mock")
public class MockPayableService implements PayableService {

    private final MockDataStore store;

    public MockPayableService(MockDataStore store) {
        this.store = store;
    }

    @Override
    public List<PayableResponse> list(PayableStatus status, LocalDate from, LocalDate to) {
        return store.getPayables().values().stream()
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
    public PayableResponse create(CreatePayableRequest request) {
        PayableEntity e = new PayableEntity();
        e.setId(store.nextPayableId());
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        e.setStatus(PayableStatus.PENDENTE);
        store.getPayables().put(e.getId(), e);
        return EntityMapper.toPayableResponse(e);
    }

    @Override
    public PayableResponse update(Long id, UpdatePayableRequest request) {
        PayableEntity e = find(id);
        if (e.getStatus() == PayableStatus.PAGA) {
            throw new com.gestorpyme.exception.BusinessException("Conta já paga não pode ser editada");
        }
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        return EntityMapper.toPayableResponse(e);
    }

    @Override
    public PayableResponse markPaid(Long id, MarkPaidRequest request) {
        PayableEntity e = find(id);
        e.setStatus(PayableStatus.PAGA);
        e.setDataPagamento(request != null && request.dataPagamento() != null
                ? request.dataPagamento() : LocalDate.now());
        return EntityMapper.toPayableResponse(e);
    }

    private PayableEntity find(Long id) {
        PayableEntity e = store.getPayables().get(id);
        if (e == null) throw new ResourceNotFoundException("Conta a pagar não encontrada: " + id);
        return e;
    }
}
