package com.kodus.demo.application.port.out;

import com.kodus.demo.domain.model.PaymentId;
import com.kodus.demo.domain.model.PaymentOrder;

import java.util.Optional;

public interface SavePaymentPort {
    PaymentOrder save(PaymentOrder order, String idempotencyKey);
    Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey);
    Optional<PaymentOrder> findById(PaymentId id);
}
