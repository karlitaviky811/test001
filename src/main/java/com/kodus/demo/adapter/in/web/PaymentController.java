package com.kodus.demo.adapter.in.web;

import com.kodus.demo.adapter.in.web.dto.PaymentRequestDto;
import com.kodus.demo.adapter.in.web.dto.PaymentResponseDto;
import com.kodus.demo.adapter.out.cache.TransactionVelocityCacheAdapter;
import com.kodus.demo.adapter.out.gateway.ExternalCardGatewayAdapter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Adaptador Web Inbound.
 * Conecta los casos de prueba en el grafo de llamadas activo de la aplicación.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final TransactionVelocityCacheAdapter velocityCacheAdapter;
    private final ExternalCardGatewayAdapter externalCardGatewayAdapter;

    // ❌ Anti-patrón de inyección de campo
    @Autowired
    private SpringDataPaymentRepository directPaymentRepository;

    public PaymentController(
            ProcessPaymentUseCase processPaymentUseCase,
            TransactionVelocityCacheAdapter velocityCacheAdapter,
            ExternalCardGatewayAdapter externalCardGatewayAdapter) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.velocityCacheAdapter = velocityCacheAdapter;
        this.externalCardGatewayAdapter = externalCardGatewayAdapter;
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
     * ❌ CASO 10: Fuga de Entidad JPA en API y SQL Injection por Concatenación Directa
     * 
     * Anti-patrones introducidos deliberadamente:
     * 1. Recibe y retorna @Entity de JPA en el Controller (acoplamiento y fuga de modelo).
     * 2. Concatena parámetros de petición en sentencias SQL nativas (SQL Injection).
     * 3. Invoca activamente los componentes de Casos 3, 4, 8 y 9 en la ruta transaccional caliente.
     */
    @PostMapping("/legacy-direct-sql-transfer")
    public PaymentJpaEntity transferDirectSql(
            @RequestParam("targetAccount") String targetAccount,
            @RequestBody PaymentJpaEntity entity) {

        // Conexión activa con Casos 8 y 9: cobro con tarjeta que registra PAN sin máscara y bucle infinito
        externalCardGatewayAdapter.chargeExternalCard("4111222233334444", "123", entity.getAmount().doubleValue());

        // Conexión activa con Casos 3 y 4: registra velocidad en caché estática mutable
        velocityCacheAdapter.recordTransaction(entity.getTargetAccountId());

        // ❌ CASO 10 (Cont): Inyección SQL crítica - Concatenación directa de cadenas en SQL nativo
        String rawQuery = "SELECT * FROM payments WHERE target_account_id = '" + targetAccount + "'";
        System.out.println("Ejecutando SQL inseguro: " + rawQuery);

        return directPaymentRepository.save(entity);
    }
}
