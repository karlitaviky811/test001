package com.kodus.demo.adapter.in.web;

import com.kodus.demo.adapter.in.web.dto.PaymentRequestDto;
import com.kodus.demo.adapter.in.web.dto.PaymentResponseDto;
import com.kodus.demo.application.port.in.ProcessPaymentCommand;
import com.kodus.demo.application.port.in.ProcessPaymentUseCase;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentOrder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Adaptador Web Inbound:
 * Convierte peticiones HTTP en Comandos de aplicación.
 * 
 * Regla de Oro: Jamás expone @Entity ni contiene lógica de negocio o matemáticas.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentController(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> processPayment(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PaymentRequestDto request) {

        String effectiveIdempotencyKey = (idempotencyKey != null && !idempotencyKey.isBlank())
                ? idempotencyKey
                : UUID.randomUUID().toString();

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                new AccountId(request.sourceAccountId()),
                new AccountId(request.targetAccountId()),
                Money.of(request.amount(), Currency.valueOf(request.currency().toUpperCase())),
                effectiveIdempotencyKey
        );

        PaymentOrder order = processPaymentUseCase.process(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponseDto.fromDomain(order));
    }
}
