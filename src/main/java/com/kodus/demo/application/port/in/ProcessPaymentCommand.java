package com.kodus.demo.application.port.in;

import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Money;

import java.util.Objects;

public record ProcessPaymentCommand(
        AccountId sourceAccountId,
        AccountId targetAccountId,
        Money amount,
        String idempotencyKey
) {
    public ProcessPaymentCommand {
        Objects.requireNonNull(sourceAccountId, "sourceAccountId cannot be null");
        Objects.requireNonNull(targetAccountId, "targetAccountId cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey cannot be null");
        if (idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey cannot be blank");
        }
    }
}
