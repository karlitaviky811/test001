package com.kodus.demo.domain.exception;

public class InsufficientFundsException extends BusinessRuleViolationException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
