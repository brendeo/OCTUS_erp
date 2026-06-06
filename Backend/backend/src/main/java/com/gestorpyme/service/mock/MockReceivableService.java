package com.gestorpyme.service.mock;

import com.gestorpyme.domain.entity.ReceivableEntity;
import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.request.CreateReceivableRequest;
import com.gestorpyme.dto.request.MarkReceivedRequest;
import com.gestorpyme.dto.request.UpdateReceivableRequest;
import com.gestorpyme.dto.response.ReceivableResponse;
import com.gestorpyme.exception.ResourceNotFoundException;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.mock.MockDataStore;
import com.gestorpyme.service.ReceivableService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Profile("mock")
public class MockReceivableService implements ReceivableService {

    private final MockDataStore store;

    public MockReceivableService(MockDataStore store) {
        this.store = store;
    }

    @Override
    public List<ReceivableResponse> list(ReceivableStatus status, LocalDate from, LocalDate to) {
        return store.getReceivables().values().stream()
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
    public ReceivableResponse create(CreateReceivableRequest request) {
        ReceivableEntity e = new ReceivableEntity();
        e.setId(store.nextReceivableId());
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        e.setStatus(ReceivableStatus.PENDENTE);
        store.getReceivables().put(e.getId(), e);
        return EntityMapper.toReceivableResponse(e);
    }

    @Override
    public ReceivableResponse update(Long id, UpdateReceivableRequest request) {
        ReceivableEntity e = find(id);
        if (e.getStatus() == ReceivableStatus.RECEBIDA) {
            throw new com.gestorpyme.exception.BusinessException("Conta já recebida não pode ser editada");
        }
        e.setDescricao(request.descricao());
        e.setValor(request.valor());
        e.setVencimento(request.vencimento());
        return EntityMapper.toReceivableResponse(e);
    }

    @Override
    public ReceivableResponse markReceived(Long id, MarkReceivedRequest request) {
        ReceivableEntity e = find(id);
        e.setStatus(ReceivableStatus.RECEBIDA);
        e.setDataRecebimento(request != null && request.dataRecebimento() != null
                ? request.dataRecebimento() : LocalDate.now());
        return EntityMapper.toReceivableResponse(e);
    }

    private ReceivableEntity find(Long id) {
        ReceivableEntity e = store.getReceivables().get(id);
        if (e == null) throw new ResourceNotFoundException("Conta a receber não encontrada: " + id);
        return e;
    }
}
