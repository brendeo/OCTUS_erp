package com.gestorpyme.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String nome,
        String categoria,
        String unidade,
        BigDecimal precoReferencia,
        Integer estoqueMinimo,
        Integer saldoAtual,
        boolean ativo,
        boolean estoqueBaixo
) {}
