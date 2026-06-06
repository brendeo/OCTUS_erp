package com.gestorpyme.repository;

import com.gestorpyme.domain.entity.StockMovementEntity;
import com.gestorpyme.domain.enums.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile({"dev", "prod", "test"})
public interface StockMovementRepository extends JpaRepository<StockMovementEntity, Long> {
    List<StockMovementEntity> findByProductIdOrderByDataMovimentoDesc(Long productId);
    List<StockMovementEntity> findByProductIdAndDataMovimentoBetween(Long productId, LocalDate from, LocalDate to);
    List<StockMovementEntity> findByDataMovimentoBetween(LocalDate from, LocalDate to);
}
