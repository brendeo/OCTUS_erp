package com.gestorpyme.service;

import com.gestorpyme.domain.enums.ReceivableStatus;
import com.gestorpyme.dto.request.CreateReceivableRequest;
import com.gestorpyme.dto.request.MarkReceivedRequest;
import com.gestorpyme.dto.request.UpdateReceivableRequest;
import com.gestorpyme.dto.response.ReceivableResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReceivableService {
    List<ReceivableResponse> list(ReceivableStatus status, LocalDate from, LocalDate to);
    ReceivableResponse getById(Long id);
    ReceivableResponse create(CreateReceivableRequest request);
    ReceivableResponse update(Long id, UpdateReceivableRequest request);
    ReceivableResponse markReceived(Long id, MarkReceivedRequest request);
}
