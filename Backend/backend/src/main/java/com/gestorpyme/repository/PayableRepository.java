package com.gestorpyme.repository;

import com.gestorpyme.domain.entity.PayableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile({"dev", "prod", "test"})
public interface PayableRepository extends JpaRepository<PayableEntity, Long> {
    List<PayableEntity> findByVencimentoBetween(LocalDate from, LocalDate to);
}
