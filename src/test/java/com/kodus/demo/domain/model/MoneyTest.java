package com.kodus.demo.domain.model;

import com.kodus.demo.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Debe redondear con HALF_EVEN a 2 decimales y operar montos con precisión")
    void shouldHandlePrecisionAndRoundingCorrectly() {
        Money m1 = Money.of(new BigDecimal("10.555"), Currency.USD);
        Money m2 = Money.of(new BigDecimal("20.100"), Currency.USD);

        assertThat(m1.getAmount()).isEqualByComparingTo("10.56");

        Money sum = m1.add(m2);
        assertThat(sum.getAmount()).isEqualByComparingTo("30.66");
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intentan operar monedas distintas")
    void shouldThrowExceptionWhenOperatingDifferentCurrencies() {
        Money usd = Money.of(new BigDecimal("100.00"), Currency.USD);
        Money eur = Money.of(new BigDecimal("50.00"), Currency.EUR);

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Cannot operate with different currencies");
    }

    @Test
    @DisplayName("Debe impedir restas que den como resultado saldo negativo")
    void shouldPreventNegativeResultInSubtract() {
        Money small = Money.of(new BigDecimal("20.00"), Currency.USD);
        Money large = Money.of(new BigDecimal("50.00"), Currency.USD);

        assertThatThrownBy(() -> small.subtract(large))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Resulting money amount cannot be negative");
    }
}
