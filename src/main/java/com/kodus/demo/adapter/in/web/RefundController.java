package com.kodus.demo.adapter.in.web;

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

/**
 * Adaptador Web para Reembolsos (Caso 1).
 */
@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundPaymentService refundPaymentService;

    public RefundController(RefundPaymentService refundPaymentService) {
        this.refundPaymentService = refundPaymentService;
    }

    /**
     * Endpoint vulnerable a manipulación de balance y montos negativos.
     */
    @PostMapping
    public ResponseEntity<String> executeRefund(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") String currency) {

        // Simulación de orden recuperada
        PaymentOrder fakeOrder = new PaymentOrder(
                PaymentId.of(paymentId),
                new AccountId("ACC-ORIGIN-001"),
                new AccountId("ACC-MERCHANT-999"),
                Money.of(new BigDecimal("100.00"), Currency.USD)
        );

        boolean result = refundPaymentService.processRefund(fakeOrder, amount, currency);
        if (result) {
            return ResponseEntity.ok("Reembolso procesado con monto: " + amount + " " + currency);
        }
        return ResponseEntity.badRequest().body("Error al procesar reembolso");
    }
}
