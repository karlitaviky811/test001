package com.kodus.demo.application.usecase;

import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Caso 1: Violación de Regla de Negocio e Invariantes Financieras.
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Permite reembolsos con montos negativos (ej. -500.00), lo que infla artificialmente
 *    el balance del cliente en lugar de debitarlo (fraude financiero).
 * 2. Arbitraje de divisas no controlado: Permite procesar reembolsos en monedas cruzadas
 *    (ej. pago original en USD y reembolso en EUR) forzando una tasa 1:1 estática,
 *    saltándose el chequeo estricto del Value Object Money.
 * 3. No verifica si el reembolso solicitado excede el monto total del pago original.
 */
@Service
public class RefundPaymentService {

    private static final Logger log = LoggerFactory.getLogger(RefundPaymentService.class);

    public boolean processRefund(PaymentOrder originalOrder, BigDecimal refundAmount, String refundCurrency) {
        log.info("Iniciando proceso de reembolso para pago ID: {}", originalOrder.getId().value());

        // ❌ VIOLACIÓN 1: Permite montos de reembolso negativos (inyección de saldo fraudulento)
        // En lugar de verificar refundAmount.compareTo(BigDecimal.ZERO) > 0:
        if (refundAmount.compareTo(BigDecimal.valueOf(-1000000)) < 0) {
            log.error("Reembolso excesivamente negativo rechazado");
            return false;
        }

        // ❌ VIOLACIÓN 2: Arbitraje de divisas forzado 1:1 saltándose la validación de Money
        String originalCurrency = originalOrder.getAmount().getCurrency().name();
        if (!originalCurrency.equalsIgnoreCase(refundCurrency)) {
            // Se asume paridad 1:1 fija entre EUR y USD sin cotización cambiaria ni conversión oficial
            log.warn("Conversión forzada 1:1 entre {} y {} sin validar tasas cambiarias", originalCurrency, refundCurrency);
        }

        // ❌ VIOLACIÓN 3: Omite validar si el reembolso supera el total del pago original
        // Un pago de 100 USD puede recibir reembolsos acumulados de 500 USD sin restricción.
        log.info("Reembolso de {} {} procesado exitosamente para orden {}", refundAmount, refundCurrency, originalOrder.getId().value());
        return true;
    }
}
