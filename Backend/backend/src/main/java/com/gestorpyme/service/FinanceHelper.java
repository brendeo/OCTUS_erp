package com.gestorpyme.service;

import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.response.PayableResponse;
import com.gestorpyme.dto.response.ReceivableResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class FinanceHelper {

    private FinanceHelper() {}

    public static BigDecimal totalPayablesOpen(List<PayableResponse> payables) {
        return payables.stream()
                .filter(p -> p.status() == PayableStatus.PENDENTE || p.status() == PayableStatus.VENCIDA)
                .map(PayableResponse::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal totalReceivablesOpen(List<ReceivableResponse> receivables) {
        return receivables.stream()
                .filter(r -> r.status() == ReceivableStatus.PENDENTE || r.status() == ReceivableStatus.VENCIDA)
                .map(ReceivableResponse::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal cashInPeriod(List<ReceivableResponse> receivables, LocalDate from, LocalDate to) {
        return receivables.stream()
                .filter(r -> r.status() == ReceivableStatus.RECEBIDA)
                .filter(r -> r.dataRecebimento() != null)
                .filter(r -> !r.dataRecebimento().isBefore(from) && !r.dataRecebimento().isAfter(to))
                .map(ReceivableResponse::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal cashOutPeriod(List<PayableResponse> payables, LocalDate from, LocalDate to) {
        return payables.stream()
                .filter(p -> p.status() == PayableStatus.PAGA)
                .filter(p -> p.dataPagamento() != null)
                .filter(p -> !p.dataPagamento().isBefore(from) && !p.dataPagamento().isAfter(to))
                .map(PayableResponse::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
