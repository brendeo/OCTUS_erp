package com.gestorpyme.controller;

import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.dto.request.CreatePayableRequest;
import com.gestorpyme.dto.request.MarkPaidRequest;
import com.gestorpyme.dto.request.UpdatePayableRequest;
import com.gestorpyme.dto.response.PayableResponse;
import com.gestorpyme.service.PayableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payables")
@Tag(name = "Payables")
@PreAuthorize("hasAnyRole('GESTOR','OPERADOR','CONTABIL')")
public class PayableController {

    private final PayableService payableService;

    public PayableController(PayableService payableService) {
        this.payableService = payableService;
    }

    @GetMapping
    public List<PayableResponse> list(
            @RequestParam(required = false) PayableStatus status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return payableService.list(status, from, to);
    }

    @GetMapping("/{id}")
    public PayableResponse get(@PathVariable Long id) {
        return payableService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayableResponse create(@Valid @RequestBody CreatePayableRequest request) {
        return payableService.create(request);
    }

    @PutMapping("/{id}")
    public PayableResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePayableRequest request) {
        return payableService.update(id, request);
    }

    @PatchMapping("/{id}/mark-paid")
    public PayableResponse markPaid(@PathVariable Long id, @RequestBody(required = false) MarkPaidRequest request) {
        return payableService.markPaid(id, request);
    }
}
