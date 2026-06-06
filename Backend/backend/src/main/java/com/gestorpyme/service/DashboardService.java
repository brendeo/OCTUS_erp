package com.gestorpyme.service;

import com.gestorpyme.dto.response.CashFlowResponse;
import com.gestorpyme.dto.response.DashboardResponse;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResponse getDashboard();
    CashFlowResponse getCashFlow(LocalDate from, LocalDate to);
}
