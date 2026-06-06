package com.gestorpyme.mock;

import com.gestorpyme.domain.entity.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Profile("mock")
public class MockDataStore {

    public static final String DEFAULT_USER = "admin";

    private final AtomicLong productIdSeq = new AtomicLong(0);
    private final AtomicLong movementIdSeq = new AtomicLong(0);
    private final AtomicLong payableIdSeq = new AtomicLong(0);
    private final AtomicLong receivableIdSeq = new AtomicLong(0);
    private final AtomicLong userIdSeq = new AtomicLong(0);

    private final Map<Long, ProductEntity> products = new ConcurrentHashMap<>();
    private final Map<Long, StockMovementEntity> movements = new ConcurrentHashMap<>();
    private final Map<Long, PayableEntity> payables = new ConcurrentHashMap<>();
    private final Map<Long, ReceivableEntity> receivables = new ConcurrentHashMap<>();
    private final Map<Long, UserEntity> users = new ConcurrentHashMap<>();

    public long nextProductId() { return productIdSeq.incrementAndGet(); }
    public long nextMovementId() { return movementIdSeq.incrementAndGet(); }
    public long nextPayableId() { return payableIdSeq.incrementAndGet(); }
    public long nextReceivableId() { return receivableIdSeq.incrementAndGet(); }
    public long nextUserId() { return userIdSeq.incrementAndGet(); }

    public Map<Long, ProductEntity> getProducts() { return products; }
    public Map<Long, StockMovementEntity> getMovements() { return movements; }
    public Map<Long, PayableEntity> getPayables() { return payables; }
    public Map<Long, ReceivableEntity> getReceivables() { return receivables; }
    public Map<Long, UserEntity> getUsers() { return users; }
}
