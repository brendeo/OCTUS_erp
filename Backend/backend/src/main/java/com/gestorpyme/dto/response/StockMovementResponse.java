package com.gestorpyme.dto.response;

import com.gestorpyme.domain.enums.MovementType;

import java.time.LocalDate;

public record StockMovementResponse(
        Long id,
        Long productId,
        MovementType tipo,
        Integer quantidade,
        LocalDate data,
        String observacao,
        String createdBy,
        ProductResponse produto
) {}
