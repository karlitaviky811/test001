package com.kodus.demo.domain.model;

import com.kodus.demo.domain.exception.BusinessRuleViolationException;
import com.kodus.demo.domain.exception.DailyLimitExceededException;
import com.kodus.demo.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentOrderTest {

    @Test
    @DisplayName("Debe crear orden PENDING con cuentas válidas y monto positivo")
    void shouldCreatePendingOrder() {
        PaymentOrder order = new PaymentOrder(
                PaymentId.generate(),
                new AccountId("ACC-1"),
                new AccountId("ACC-2"),
                Money.of(new BigDecimal("100.00"), Currency.USD)
        );

        assertThat(order.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe fallar al crear orden si origen y destino son la misma cuenta")
    void shouldFailWhenSourceAndTargetAreSame() {
        assertThatThrownBy(() -> new PaymentOrder(
                PaymentId.generate(),
                new AccountId("ACC-1"),
                new AccountId("ACC-1"),
                Money.of(new BigDecimal("100.00"), Currency.USD)
        )).isInstanceOf(BusinessRuleViolationException.class)
          .hasMessageContaining("Source and target account cannot be the same");
    }

    @Test
    @DisplayName("Regla de Negocio: Debe rechazar si saldo es insuficiente")
    void shouldRejectWhenInsufficientFunds() {
        PaymentOrder order = new PaymentOrder(
                PaymentId.generate(),
                new AccountId("ACC-1"),
                new AccountId("ACC-2"),
                Money.of(new BigDecimal("500.00"), Currency.USD)
        );

        Money currentBalance = Money.of(new BigDecimal("100.00"), Currency.USD);
        Money dailySpent = Money.of(new BigDecimal("0.00"), Currency.USD);
        Money dailyLimit = Money.of(new BigDecimal("2000.00"), Currency.USD);

        assertThatThrownBy(() -> order.authorize(currentBalance, dailySpent, dailyLimit))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(order.getStatus()).isEqualTo(PaymentStatus.REJECTED);
        assertThat(order.getRejectionReason()).contains("Fondos insuficientes");
    }

    @Test
    @DisplayName("Regla de Negocio: Debe rechazar si se excede el límite diario")
    void shouldRejectWhenDailyLimitExceeded() {
        PaymentOrder order = new PaymentOrder(
                PaymentId.generate(),
                new AccountId("ACC-1"),
                new AccountId("ACC-2"),
                Money.of(new BigDecimal("300.00"), Currency.USD)
        );

        Money currentBalance = Money.of(new BigDecimal("5000.00"), Currency.USD);
        Money dailySpent = Money.of(new BigDecimal("1800.00"), Currency.USD);
        Money dailyLimit = Money.of(new BigDecimal("2000.00"), Currency.USD);

        assertThatThrownBy(() -> order.authorize(currentBalance, dailySpent, dailyLimit))
                .isInstanceOf(DailyLimitExceededException.class);

        assertThat(order.getStatus()).isEqualTo(PaymentStatus.REJECTED);
        assertThat(order.getRejectionReason()).contains("Límite diario");
    }

    @Test
    @DisplayName("Debe autorizar y luego liquidar exitosamente cumpliendo todas las invariantes")
    void shouldAuthorizeAndSettleSuccessfully() {
        PaymentOrder order = new PaymentOrder(
                PaymentId.generate(),
                new AccountId("ACC-1"),
                new AccountId("ACC-2"),
                Money.of(new BigDecimal("150.00"), Currency.USD)
        );

        Money currentBalance = Money.of(new BigDecimal("1000.00"), Currency.USD);
        Money dailySpent = Money.of(new BigDecimal("200.00"), Currency.USD);
        Money dailyLimit = Money.of(new BigDecimal("2000.00"), Currency.USD);

        order.authorize(currentBalance, dailySpent, dailyLimit);
        assertThat(order.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

        order.settle();
        assertThat(order.getStatus()).isEqualTo(PaymentStatus.SETTLED);
    }
}
