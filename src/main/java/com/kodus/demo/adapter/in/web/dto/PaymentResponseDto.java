package com.kodus.demo.adapter.in.web.dto;

import com.kodus.demo.domain.model.PaymentOrder;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponseDto(
        String paymentId,
        String sourceAccountId,
        String targetAccountId,
        BigDecimal amount,
        String currency,
        String status,
        String rejectionReason,
        Instant createdAt
) {
    public static PaymentResponseDto fromDomain(PaymentOrder order) {
        return new PaymentResponseDto(
                order.getId().value().toString(),
                order.getSourceAccountId().value(),
                order.getTargetAccountId().value(),
                order.getAmount().getAmount(),
                order.getAmount().getCurrency().name(),
                order.getStatus().name(),
                order.getRejectionReason(),
                order.getCreatedAt()
        );
    }
}
