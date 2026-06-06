package com.gestorpyme.repository;

import com.gestorpyme.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"dev", "prod", "test"})
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
