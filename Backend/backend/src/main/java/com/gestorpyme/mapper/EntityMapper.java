package com.gestorpyme.mapper;

import com.gestorpyme.domain.entity.*;
import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.response.*;

import java.time.LocalDate;

public final class EntityMapper {

    private EntityMapper() {}

    public static ProductResponse toProductResponse(ProductEntity e) {
        boolean estoqueBaixo = e.getSaldoAtual() <= e.getEstoqueMinimo();
        return new ProductResponse(
                e.getId(), e.getNome(), e.getCategoria(), e.getUnidade(),
                e.getPrecoReferencia(), e.getEstoqueMinimo(), e.getSaldoAtual(),
                e.isAtivo(), estoqueBaixo
        );
    }

    public static StockMovementResponse toMovementResponse(StockMovementEntity m) {
        return new StockMovementResponse(
                m.getId(),
                m.getProduct().getId(),
                m.getTipo(),
                m.getQuantidade(),
                m.getDataMovimento(),
                m.getObservacao(),
                m.getCreatedBy(),
                toProductResponse(m.getProduct())
        );
    }

    public static PayableResponse toPayableResponse(PayableEntity e) {
        PayableStatus status = resolvePayableStatus(e);
        return new PayableResponse(
                e.getId(), e.getDescricao(), e.getValor(), e.getVencimento(),
                status, e.getDataPagamento()
        );
    }

    public static ReceivableResponse toReceivableResponse(ReceivableEntity e) {
        ReceivableStatus status = resolveReceivableStatus(e);
        return new ReceivableResponse(
                e.getId(), e.getDescricao(), e.getValor(), e.getVencimento(),
                status, e.getDataRecebimento()
        );
    }

    public static UserResponse toUserResponse(UserEntity u) {
        return new UserResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
    }

    public static PayableStatus resolvePayableStatus(PayableEntity e) {
        if (e.getStatus() == PayableStatus.PAGA) {
            return PayableStatus.PAGA;
        }
        if (e.getVencimento().isBefore(LocalDate.now())) {
            return PayableStatus.VENCIDA;
        }
        return PayableStatus.PENDENTE;
    }

    public static ReceivableStatus resolveReceivableStatus(ReceivableEntity e) {
        if (e.getStatus() == ReceivableStatus.RECEBIDA) {
            return ReceivableStatus.RECEBIDA;
        }
        if (e.getVencimento().isBefore(LocalDate.now())) {
            return ReceivableStatus.VENCIDA;
        }
        return ReceivableStatus.PENDENTE;
    }
}
