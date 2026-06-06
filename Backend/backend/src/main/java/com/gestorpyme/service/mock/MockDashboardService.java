package com.gestorpyme.service.mock;

import com.gestorpyme.domain.entity.ProductEntity;
import com.gestorpyme.dto.response.CashFlowResponse;
import com.gestorpyme.dto.response.DashboardResponse;
import com.gestorpyme.dto.response.PayableResponse;
import com.gestorpyme.dto.response.ReceivableResponse;
import com.gestorpyme.mapper.EntityMapper;
import com.gestorpyme.mock.MockDataStore;
import com.gestorpyme.service.DashboardService;
import com.gestorpyme.service.FinanceHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("mock")
public class MockDashboardService implements DashboardService {

    private final MockDataStore store;
    private final MockPayableService payableService;
    private final MockReceivableService receivableService;

    public MockDashboardService(MockDataStore store, MockPayableService payableService, MockReceivableService receivableService) {
        this.store = store;
        this.payableService = payableService;
        this.receivableService = receivableService;
    }

    @Override
    public DashboardResponse getDashboard() {
        long totalProdutos = store.getProducts().values().stream().filter(ProductEntity::isAtivo).count();
        long estoqueBaixo = store.getProducts().values().stream()
                .filter(p -> p.isAtivo() && p.getSaldoAtual() <= p.getEstoqueMinimo())
                .count();
        List<PayableResponse> payables = payableService.list(null, null, null);
        List<ReceivableResponse> receivables = receivableService.list(null, null, null);
        BigDecimal totalPagar = FinanceHelper.totalPayablesOpen(payables);
        BigDecimal totalReceber = FinanceHelper.totalReceivablesOpen(receivables);
        return new DashboardResponse(totalProdutos, estoqueBaixo, totalPagar, totalReceber, totalReceber.subtract(totalPagar));
    }

    @Override
    public CashFlowResponse getCashFlow(LocalDate from, LocalDate to) {
        LocalDate start = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate end = to != null ? to : LocalDate.now();
        List<PayableResponse> payables = payableService.list(null, null, null);
        List<ReceivableResponse> receivables = receivableService.list(null, null, null);
        BigDecimal entradas = FinanceHelper.cashInPeriod(receivables, start, end);
        BigDecimal saidas = FinanceHelper.cashOutPeriod(payables, start, end);
        return new CashFlowResponse(start, end, entradas, saidas, entradas.subtract(saidas));
    }
}
