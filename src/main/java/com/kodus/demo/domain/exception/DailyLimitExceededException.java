package com.kodus.demo.domain.exception;

public class DailyLimitExceededException extends BusinessRuleViolationException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
