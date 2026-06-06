package com.gestorpyme.controller;

import com.gestorpyme.domain.enums.MovementType;
import com.gestorpyme.dto.request.CreateMovementRequest;
import com.gestorpyme.dto.request.CreateProductRequest;
import com.gestorpyme.dto.request.UpdateProductRequest;
import com.gestorpyme.dto.response.ProductPageResponse;
import com.gestorpyme.dto.response.ProductResponse;
import com.gestorpyme.dto.response.StockMovementResponse;
import com.gestorpyme.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products")
@PreAuthorize("hasAnyRole('GESTOR','OPERADOR','ESTOQUE')")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ProductPageResponse list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productService.list(search, category, active, page, size);
    }

    @GetMapping("/alerts/low-stock")
    public List<ProductResponse> lowStock() {
        return productService.lowStockAlerts();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public ProductResponse deactivate(@PathVariable Long id) {
        return productService.deactivate(id);
    }

    @GetMapping("/{productId}/movements")
    @Operation(summary = "Movimentações do produto")
    public List<StockMovementResponse> movements(
            @PathVariable Long productId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) MovementType type) {
        return productService.listMovements(productId, from, to, type);
    }

    @PostMapping("/{productId}/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementResponse createMovement(
            @PathVariable Long productId,
            @Valid @RequestBody CreateMovementRequest request) {
        return productService.createMovement(productId, request);
    }
}
