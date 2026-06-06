package com.gestorpyme.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashFlowResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldo
) {}
