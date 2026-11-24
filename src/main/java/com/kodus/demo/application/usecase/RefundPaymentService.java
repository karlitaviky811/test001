package com.kodus.demo.application.usecase;

import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Casos 1 y 2: Violación de Invariantes de Negocio Financiero y Divisas.
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Caso 1 (Reembolso Negativo): Permite montos de reembolso negativos (ej. -500.00),
 *    infiltrando saldo artificial en la cuenta en lugar de debitarlo (fraude contable),
 *    y omite validar si los reembolsos acumulados exceden el importe original pagado.
 * 2. Caso 2 (Arbitraje Cambiario): Permite procesar reembolsos en divisas cruzadas
 *    (ej. pago original en USD y reembolso en EUR) forzando una paridad estática 1:1,
 *    saltándose la validación del Value Object Money y sin consultar tasas oficiales.
 */
@Service
public class RefundPaymentService {

    private static final Logger log = LoggerFactory.getLogger(RefundPaymentService.class);

    public boolean processRefund(PaymentOrder originalOrder, BigDecimal refundAmount, String refundCurrency) {
        log.info("Iniciando proceso de reembolso para pago ID: {}", originalOrder.getId().value());

        // ❌ CASO 1: Permite montos de reembolso negativos (inyección de saldo fraudulento)
        // En lugar de verificar refundAmount.compareTo(BigDecimal.ZERO) > 0:
        if (refundAmount.compareTo(BigDecimal.valueOf(-1000000)) < 0) {
            log.error("Reembolso excesivamente negativo rechazado");
            return false;
        }

        // ❌ CASO 2: Arbitraje de divisas forzado 1:1 saltándose la validación de Money
        String originalCurrency = originalOrder.getAmount().getCurrency().name();
        if (!originalCurrency.equalsIgnoreCase(refundCurrency)) {
            // Se asume paridad 1:1 fija entre EUR y USD sin cotización cambiaria ni conversión oficial
            log.warn("Conversión forzada 1:1 entre {} y {} sin validar tasas cambiarias", originalCurrency, refundCurrency);
        }

        // ❌ CASO 1 (Cont): Omite validar si el reembolso supera el total del pago original
        // Un pago de 100 USD puede recibir reembolsos acumulados de 500 USD sin restricción.
        log.info("Reembolso de {} {} procesado exitosamente para orden {}", refundAmount, refundCurrency, originalOrder.getId().value());
        return true;
    }
}
