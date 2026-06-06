package com.gestorpyme.service;

import com.gestorpyme.domain.enums.PayableStatus;
import com.gestorpyme.dto.request.CreatePayableRequest;
import com.gestorpyme.dto.request.MarkPaidRequest;
import com.gestorpyme.dto.request.UpdatePayableRequest;
import com.gestorpyme.dto.response.PayableResponse;

import java.time.LocalDate;
import java.util.List;

public interface PayableService {
    List<PayableResponse> list(PayableStatus status, LocalDate from, LocalDate to);
    PayableResponse getById(Long id);
    PayableResponse create(CreatePayableRequest request);
    PayableResponse update(Long id, UpdatePayableRequest request);
    PayableResponse markPaid(Long id, MarkPaidRequest request);
}
