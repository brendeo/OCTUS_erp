package com.gestorpyme.dto.request;

import com.gestorpyme.domain.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateMovementRequest(
        @NotNull MovementType tipo,
        @NotNull @Positive Integer quantidade,
        @NotNull LocalDate data,
        String observacao
) {}
