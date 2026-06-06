package com.gestorpyme.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank String nome,
        @NotBlank String categoria,
        @NotBlank String unidade,
        @NotNull @PositiveOrZero BigDecimal precoReferencia,
        @NotNull @PositiveOrZero Integer estoqueMinimo
) {}
