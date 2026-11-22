package com.kodus.demo.domain.model;

import com.kodus.demo.domain.exception.BusinessRuleViolationException;
import com.kodus.demo.domain.exception.DailyLimitExceededException;
import com.kodus.demo.domain.exception.InsufficientFundsException;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad raíz de agregado (Aggregate Root) que encapsula todas las invariantes
 * y transiciones de estado de un pago.
 * 
 * Regla de Oro: El dominio no tiene setters anémicos; todo cambio de estado
 * es validado por métodos con significado de negocio.
 */
public class PaymentOrder {

    private final PaymentId id;
    private final AccountId sourceAccountId;
    private final AccountId targetAccountId;
    private final Money amount;
    private PaymentStatus status;
    private String rejectionReason;
    private final Instant createdAt;
    private Instant updatedAt;

    public PaymentOrder(PaymentId id, AccountId sourceAccountId, AccountId targetAccountId, Money amount) {
        Objects.requireNonNull(id, "Payment ID is required");
        Objects.requireNonNull(sourceAccountId, "Source account ID is required");
        Objects.requireNonNull(targetAccountId, "Target account ID is required");
        Objects.requireNonNull(amount, "Amount is required");

        if (sourceAccountId.equals(targetAccountId)) {
            throw new BusinessRuleViolationException("Source and target account cannot be the same");
        }
        if (!amount.isPositive()) {
            throw new BusinessRuleViolationException("Payment amount must be strictly positive");
        }

        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Rehidratación desde persistencia (sin ejecutar validaciones de creación).
     */
    public static PaymentOrder rehydrate(
            PaymentId id,
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount,
            PaymentStatus status,
            String rejectionReason,
            Instant createdAt,
            Instant updatedAt) {
        PaymentOrder order = new PaymentOrder(id, sourceAccountId, targetAccountId, amount);
        order.status = status;
        order.rejectionReason = rejectionReason;
        order.updatedAt = updatedAt;
        return order;
    }

    /**
     * Regla de Negocio: Autorización de fondos y validación de límite diario.
     */
    public void authorize(Money currentBalance, Money dailySpent, Money dailyLimit) {
        assertNotTerminal();

        if (amount.isGreaterThan(currentBalance)) {
            reject("Fondos insuficientes en la cuenta origen");
            throw new InsufficientFundsException("Account " + sourceAccountId.value() 
                + " has balance " + currentBalance + " but requires " + amount);
        }

        Money totalAfterPayment = dailySpent.add(amount);
        if (totalAfterPayment.isGreaterThan(dailyLimit)) {
            reject("Límite diario de transferencias superado");
            throw new DailyLimitExceededException("Daily limit " + dailyLimit 
                + " exceeded by total attempt " + totalAfterPayment);
        }

        this.status = PaymentStatus.AUTHORIZED;
        this.updatedAt = Instant.now();
    }

    /**
     * Regla de Negocio: Liquidación final del pago autorizado.
     */
    public void settle() {
        if (this.status != PaymentStatus.AUTHORIZED) {
            throw new BusinessRuleViolationException(
                "Cannot settle payment in state " + this.status + ". Must be AUTHORIZED.");
        }
        this.status = PaymentStatus.SETTLED;
        this.updatedAt = Instant.now();
    }

    public void reject(String reason) {
        assertNotTerminal();
        this.status = PaymentStatus.REJECTED;
        this.rejectionReason = Objects.requireNonNull(reason, "Rejection reason cannot be null");
        this.updatedAt = Instant.now();
    }

    private void assertNotTerminal() {
        if (this.status == PaymentStatus.SETTLED || this.status == PaymentStatus.REJECTED) {
            throw new BusinessRuleViolationException(
                "Payment is already in terminal status " + this.status + " and cannot be mutated.");
        }
    }

    public PaymentId getId() {
        return id;
    }

    public AccountId getSourceAccountId() {
        return sourceAccountId;
    }

    public AccountId getTargetAccountId() {
        return targetAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
