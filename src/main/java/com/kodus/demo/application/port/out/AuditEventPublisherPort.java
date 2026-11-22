package com.kodus.demo.application.port.out;

import com.kodus.demo.domain.model.PaymentOrder;

public interface AuditEventPublisherPort {
    void publishPaymentProcessed(PaymentOrder order, String idempotencyKey);
}
