package com.kodus.demo.application.port.out;

import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Money;

import java.util.Optional;

public interface LoadAccountPort {
    Optional<AccountSnapshot> loadAccount(AccountId accountId);

    record AccountSnapshot(
            AccountId id,
            Money currentBalance,
            Money dailySpent,
            Money dailyLimit
    ) {}
}
