package com.gestorpyme.dto.response;

import com.gestorpyme.domain.enums.PayableStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PayableResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate vencimento,
        PayableStatus status,
        LocalDate dataPagamento
) {}
