package com.kodus.demo.adapter.out.account;

import com.kodus.demo.application.port.out.LoadAccountPort;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador de cuentas en memoria para permitir ejecutar la demo de inmediato
 * sin necesidad de microservicios externos de Core Bancario.
 */
@Component
public class InMemoryAccountAdapter implements LoadAccountPort {

    private final Map<String, AccountSnapshot> accounts = new ConcurrentHashMap<>();

    public InMemoryAccountAdapter() {
        // Cuentas de demostración iniciales
        // Cuenta A: $5,000 USD de saldo, $200 gastados hoy, límite diario $2,000 USD
        accounts.put("ACC-001", new AccountSnapshot(
                new AccountId("ACC-001"),
                Money.of(new BigDecimal("5000.00"), Currency.USD),
                Money.of(new BigDecimal("200.00"), Currency.USD),
                Money.of(new BigDecimal("2000.00"), Currency.USD)
        ));

        // Cuenta B (con saldo bajo): $50 USD de saldo, $0 gastados, límite $1,000 USD
        accounts.put("ACC-002", new AccountSnapshot(
                new AccountId("ACC-002"),
                Money.of(new BigDecimal("50.00"), Currency.USD),
                Money.of(new BigDecimal("0.00"), Currency.USD),
                Money.of(new BigDecimal("1000.00"), Currency.USD)
        ));

        // Cuenta C (con límite diario casi agotado): $10,000 USD saldo, $1,900 gastados hoy, límite $2,000 USD
        accounts.put("ACC-003", new AccountSnapshot(
                new AccountId("ACC-003"),
                Money.of(new BigDecimal("10000.00"), Currency.USD),
                Money.of(new BigDecimal("1900.00"), Currency.USD),
                Money.of(new BigDecimal("2000.00"), Currency.USD)
        ));
    }

    @Override
    public Optional<AccountSnapshot> loadAccount(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId.value()));
    }
}
