package com.kodus.demo.domain.model;

import java.util.Objects;

public record AccountId(String value) {
    public AccountId {
        Objects.requireNonNull(value, "Account ID cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be blank");
        }
    }
}
