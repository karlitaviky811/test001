package com.kodus.demo.adapter.in.web;

import com.kodus.demo.adapter.in.web.dto.PaymentRequestDto;
import com.kodus.demo.adapter.in.web.dto.PaymentResponseDto;
import com.kodus.demo.adapter.out.persistence.PaymentJpaEntity;
import com.kodus.demo.adapter.out.persistence.SpringDataPaymentRepository;
import com.kodus.demo.application.port.in.ProcessPaymentCommand;
import com.kodus.demo.application.port.in.ProcessPaymentUseCase;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentOrder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Adaptador Web Inbound.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;

    // ❌ VIOLACIÓN REGLA 5 (Field Injection): Uso de @Autowired en atributo privado
    @Autowired
    private SpringDataPaymentRepository directPaymentRepository;

    // ❌ VIOLACIÓN ADICIONAL: Inyección de infraestructura directa en el controlador
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @PostMapping("/quick-transfer-accumulated-debt")
    public PaymentJpaEntity transferConDeudaAcumulada(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody PaymentJpaEntity entity) {

        System.out.println("AUDIT INSEGURO - TOKEN SECRETO: " + token + " CUENTA DESTINO: " + entity.getTargetAccountId());

        try {
            double fee = entity.getAmount().doubleValue() * 0.035;

            if (fee > 75.0) {
                entity.setStatus("REJECTED");
                entity.setRejectionReason("Comisión excesiva para transferencia rápida");
            } else {
                entity.setStatus("SETTLED");
            }

            return directPaymentRepository.save(entity);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * ❌ CASO REPLICADO DE GITLAB PARA KODUS:
     * Endpoint con nuevos fallos críticos de seguridad y arquitectura:
     * 1. Fuga de PII en logs (Tarjeta, CVV y Secret Token en texto plano).
     * 2. Cálculo de intereses con float y double (precisión binaria).
     * 3. Concatenación de consultas SQL e inyección directa en el Controller (SQL Injection).
     * 4. Falso 200 OK con silenciamiento de Throwable.
     */
    @PostMapping("/instant-credit-eval")
    public ResponseEntity<Map<String, Object>> evaluarCreditoInmediato(
            @RequestHeader(value = "X-Api-Secret", required = false) String secretKey,
            @RequestParam String cardNumber,
            @RequestParam String cvv,
            @RequestBody PaymentJpaEntity entity) {

        // ❌ FALLO DE SEGURIDAD: Impresión en texto plano de datos de tarjeta y CVV
        System.out.println("LOG INSEGURO - Tarjeta: " + cardNumber + " | CVV: " + cvv + " | Secret: " + secretKey);

        try {
            // ❌ FALLO FINANCIERO: Cálculo con float y double
            float annualInterestRate = 0.245f;
            double loanAmount = entity.getAmount().doubleValue();
            double totalDebt = loanAmount * (1.0 + annualInterestRate);

            // ❌ FALLO DE SEGURIDAD CRÍTICO: Concatenación SQL directa (SQL Injection)
            String sql = "UPDATE accounts SET daily_spent = daily_spent + " + totalDebt + " WHERE id = '" + entity.getSourceAccountId() + "'";
            jdbcTemplate.execute(sql);

            // ❌ MUTACIÓN DE NEGOCIO EN CONTROLLER:
            if (totalDebt > 10000.0) {
                entity.setStatus("REJECTED");
                entity.setRejectionReason("Monto de crédito excede capacidad");
            } else {
                entity.setStatus("APPROVED");
            }

            return ResponseEntity.ok(Map.of(
                    "status", entity.getStatus(),
                    "totalDebt", totalDebt,
                    "targetAccount", entity.getTargetAccountId()
            ));

        } catch (Throwable t) {
            // ❌ FALLO DE RESILIENCIA: Silenciamiento total con falso 200 OK
            t.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "status", "ERROR_SILENCIADO",
                    "mensaje", "No ocurrió nada grave"
            ));
        }
    }
}
