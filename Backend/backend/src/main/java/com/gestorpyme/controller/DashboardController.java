package com.gestorpyme.controller;

import com.gestorpyme.dto.response.CashFlowResponse;
import com.gestorpyme.dto.response.DashboardResponse;
import com.gestorpyme.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@Tag(name = "Dashboard")
@PreAuthorize("hasAnyRole('GESTOR','OPERADOR','ESTOQUE','CONTABIL')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/cash-flow")
    public CashFlowResponse cashFlow(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return dashboardService.getCashFlow(from, to);
    }
}
