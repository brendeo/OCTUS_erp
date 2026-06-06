package com.gestorpyme.controller;

import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.response.CashFlowResponse;
import com.gestorpyme.dto.response.PayableResponse;
import com.gestorpyme.dto.response.ReceivableResponse;
import com.gestorpyme.dto.response.StockMovementResponse;
import com.gestorpyme.service.DashboardService;
import com.gestorpyme.service.PayableService;
import com.gestorpyme.service.ProductService;
import com.gestorpyme.service.ReceivableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports")
@PreAuthorize("hasAnyRole('GESTOR','CONTABIL')")
public class ReportController {

    private final ProductService productService;
    private final PayableService payableService;
    private final ReceivableService receivableService;
    private final DashboardService dashboardService;

    public ReportController(ProductService productService, PayableService payableService,
                            ReceivableService receivableService, DashboardService dashboardService) {
        this.productService = productService;
        this.payableService = payableService;
        this.receivableService = receivableService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stock-movements")
    public List<StockMovementResponse> stockMovements(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return productService.allMovements(from, to);
    }

    @GetMapping("/payables")
    public List<PayableResponse> payables(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) PayableStatus status) {
        return payableService.list(status, from, to);
    }

    @GetMapping("/receivables")
    public List<ReceivableResponse> receivables(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) ReceivableStatus status) {
        return receivableService.list(status, from, to);
    }

    @GetMapping("/cash-flow")
    public CashFlowResponse cashFlow(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return dashboardService.getCashFlow(from, to);
    }
}
