package com.kodus.demo.adapter.out.audit;

import com.kodus.demo.application.port.out.AuditEventPublisherPort;
import com.kodus.demo.domain.model.PaymentOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsoleAuditAdapter implements AuditEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(ConsoleAuditAdapter.class);

    @Override
    public void publishPaymentProcessed(PaymentOrder order, String idempotencyKey) {
        log.info("[AUDIT] Payment ID: {} | Status: {} | Amount: {} | Source: {} | Target: {} | IdempotencyKey: {}",
                order.getId().value(),
                order.getStatus(),
                order.getAmount(),
                order.getSourceAccountId().value(),
                order.getTargetAccountId().value(),
                idempotencyKey);
    }
}
