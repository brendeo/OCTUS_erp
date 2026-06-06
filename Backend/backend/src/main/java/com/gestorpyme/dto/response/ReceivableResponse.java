package com.gestorpyme.dto.response;

import com.gestorpyme.domain.enums.ReceivableStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceivableResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate vencimento,
        ReceivableStatus status,
        LocalDate dataRecebimento
) {}
