package com.gestorpyme.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(
        long totalProdutos,
        long produtosEstoqueBaixo,
        BigDecimal totalAPagar,
        BigDecimal totalAReceber,
        BigDecimal saldoCaixa
) {}
