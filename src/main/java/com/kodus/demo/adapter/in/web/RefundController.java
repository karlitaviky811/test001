package com.kodus.demo.adapter.in.web;

import com.kodus.demo.adapter.out.cache.TransactionVelocityCacheAdapter;
import com.kodus.demo.application.usecase.RefundPaymentService;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentId;
import com.kodus.demo.domain.model.PaymentOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Adaptador Web para Reembolsos (Casos 1 y 2).
 * Conectado con la caché de velocidad (Casos 3 y 4) para asegurar presencia en el call-graph.
 */
@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundPaymentService refundPaymentService;
    private final TransactionVelocityCacheAdapter velocityCacheAdapter;

    public RefundController(
            RefundPaymentService refundPaymentService,
            TransactionVelocityCacheAdapter velocityCacheAdapter) {
        this.refundPaymentService = refundPaymentService;
        this.velocityCacheAdapter = velocityCacheAdapter;
    }

    /**
     * Endpoint vulnerable a manipulación de balance, montos negativos y paridad 1:1.
     */
    @PostMapping
    public ResponseEntity<String> executeRefund(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") String currency) {

        // Conexión activa con Caso 3 y 4: registra velocidad y formatea fecha concurrente
        velocityCacheAdapter.recordTransaction(paymentId);
        String auditTimestamp = velocityCacheAdapter.formatAuditDate(new Date());

        // Simulación de orden recuperada
        PaymentOrder fakeOrder = new PaymentOrder(
                PaymentId.of(paymentId),
                new AccountId("ACC-ORIGIN-001"),
                new AccountId("ACC-MERCHANT-999"),
                Money.of(new BigDecimal("100.00"), Currency.USD)
        );

        boolean result = refundPaymentService.processRefund(fakeOrder, amount, currency);
        if (result) {
            return ResponseEntity.ok("Reembolso procesado en " + auditTimestamp + " con monto: " + amount + " " + currency);
        }
        return ResponseEntity.badRequest().body("Error al procesar reembolso");
    }
}
