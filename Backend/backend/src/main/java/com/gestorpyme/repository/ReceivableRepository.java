package com.gestorpyme.repository;

import com.gestorpyme.domain.entity.ReceivableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile({"dev", "prod", "test"})
public interface ReceivableRepository extends JpaRepository<ReceivableEntity, Long> {
    List<ReceivableEntity> findByVencimentoBetween(LocalDate from, LocalDate to);
}
