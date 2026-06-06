package com.gestorpyme.service;

import com.gestorpyme.domain.enums.MovementType;
import com.gestorpyme.dto.request.CreateMovementRequest;
import com.gestorpyme.dto.request.CreateProductRequest;
import com.gestorpyme.dto.request.UpdateProductRequest;
import com.gestorpyme.dto.response.ProductPageResponse;
import com.gestorpyme.dto.response.ProductResponse;
import com.gestorpyme.dto.response.StockMovementResponse;

import java.time.LocalDate;
import java.util.List;

public interface ProductService {
    ProductPageResponse list(String search, String category, Boolean active, int page, int size);
    ProductResponse getById(Long id);
    ProductResponse create(CreateProductRequest request);
    ProductResponse update(Long id, UpdateProductRequest request);
    ProductResponse deactivate(Long id);
    List<ProductResponse> lowStockAlerts();
    List<StockMovementResponse> listMovements(Long productId, LocalDate from, LocalDate to, MovementType type);
    StockMovementResponse createMovement(Long productId, CreateMovementRequest request);
    List<StockMovementResponse> allMovements(LocalDate from, LocalDate to);
}
