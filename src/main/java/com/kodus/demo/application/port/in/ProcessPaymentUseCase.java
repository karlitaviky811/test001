package com.kodus.demo.application.port.in;

import com.kodus.demo.domain.model.PaymentOrder;

public interface ProcessPaymentUseCase {
    PaymentOrder process(ProcessPaymentCommand command);
}
