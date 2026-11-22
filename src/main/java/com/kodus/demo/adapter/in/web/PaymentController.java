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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * ❌ ENDPOINT CON DEUDA TÉCNICA ACUMULADA:
     * Contiene múltiples violaciones arquitectónicas y de negocio simultáneas:
     * 1. Recibe y retorna @Entity de JPA en el Controller (Regla no-orm-entities-in-controllers).
     * 2. Usa tipo primitivo double para cálculos financieros (Regla financial-precision-money).
     * 3. Lógica de negocio e invariantes de estado dentro del Controller (Regla business-invariant-limits).
     * 4. Silenciamiento de excepciones con catch(Exception) y printStackTrace (Regla proper-exception-handling).
     * 5. Vuelco de credenciales/PII en logs y omitir el puerto de auditoría (Regla financial-security-pii-audit).
     */
    @PostMapping("/quick-transfer-accumulated-debt")
    public PaymentJpaEntity transferConDeudaAcumulada(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody PaymentJpaEntity entity) {

        // ❌ VIOLACIÓN REGLA 7: Fuga de PII y secretos en System.out sin máscara
        System.out.println("AUDIT INSEGURO - TOKEN SECRETO: " + token + " CUENTA DESTINO: " + entity.getTargetAccountId());

        try {
            // ❌ VIOLACIÓN REGLA 2: Uso de double para comisiones monetarias
            double fee = entity.getAmount().doubleValue() * 0.035;

            // ❌ VIOLACIÓN REGLA 4: Mutación directa de estado e invariantes en la capa Web
            if (fee > 75.0) {
                entity.setStatus("REJECTED");
                entity.setRejectionReason("Comisión excesiva para transferencia rápida");
            } else {
                entity.setStatus("SETTLED");
            }

            // Guardado directo saltándose el caso de uso y el puerto de auditoría
            return directPaymentRepository.save(entity);

        } catch (Exception ex) {
            // ❌ VIOLACIÓN REGLA 6: Silenciamiento de excepciones, printStackTrace y retorno de null
            ex.printStackTrace();
            return null;
        }
    }
}
