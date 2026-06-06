package com.gestorpyme.controller;

import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.request.CreateReceivableRequest;
import com.gestorpyme.dto.request.MarkReceivedRequest;
import com.gestorpyme.dto.request.UpdateReceivableRequest;
import com.gestorpyme.dto.response.ReceivableResponse;
import com.gestorpyme.service.ReceivableService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/receivables")
@Tag(name = "Receivables")
@PreAuthorize("hasAnyRole('GESTOR','OPERADOR','CONTABIL')")
public class ReceivableController {

    private final ReceivableService receivableService;

    public ReceivableController(ReceivableService receivableService) {
        this.receivableService = receivableService;
    }

    @GetMapping
    public List<ReceivableResponse> list(
            @RequestParam(required = false) ReceivableStatus status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return receivableService.list(status, from, to);
    }

    @GetMapping("/{id}")
    public ReceivableResponse get(@PathVariable Long id) {
        return receivableService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReceivableResponse create(@Valid @RequestBody CreateReceivableRequest request) {
        return receivableService.create(request);
    }

    @PutMapping("/{id}")
    public ReceivableResponse update(@PathVariable Long id, @Valid @RequestBody UpdateReceivableRequest request) {
        return receivableService.update(id, request);
    }

    @PatchMapping("/{id}/mark-received")
    public ReceivableResponse markReceived(@PathVariable Long id, @RequestBody(required = false) MarkReceivedRequest request) {
        return receivableService.markReceived(id, request);
    }
}
