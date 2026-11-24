package com.kodus.demo.adapter.out.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

/**
 * Casos 8 y 9: Resiliencia (Thread Starvation) y Seguridad PCI-DSS (Exposición de PAN en Logs).
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Caso 8: Bucle while(true) infinito sin límite máximo de intentos (unbounded retries),
 *    sin retroceso exponencial (backoff) ni timeout, causando saturación de CPU y
 *    agotamiento del pool de hilos web (Thread Pool Starvation).
 * 2. Caso 9: Exposición de número completo de tarjeta de crédito (PAN de 16 dígitos)
 *    y código de seguridad (CVV) en texto claro en logs, violando la normativa PCI-DSS.
 */
@Component
public class ExternalCardGatewayAdapter {

    private static final Logger log = LoggerFactory.getLogger(ExternalCardGatewayAdapter.class);

    public boolean chargeExternalCard(String cardNumber, String cvv, double amount) {
        // ❌ CASO 9: Fuga de PII y datos de tarjeta de crédito completos (PAN de 16 dígitos y CVV) en logs sin máscara
        log.info("Procesando cobro en tarjeta PAN: {} CVV: {} por monto {}", cardNumber, cvv, amount);

        boolean charged = false;

        // ❌ CASO 8: Bucle infinito de reintentos sin backoff, sin timeout y sin max attempts
        while (!charged) {
            try {
                // Simulación de pasarela externa que experimenta timeout o intermitencia
                callThirdPartyCardNetwork(cardNumber, amount);
                charged = true;
            } catch (SocketTimeoutException e) {
                // Reintento continuo e inmediato sin delay ni backoff
                log.warn("Timeout con la red externa de tarjetas. Reintentando inmediatamente...");
            } catch (Exception e) {
                log.error("Fallo inesperado al conectar con pasarela externa. Reintentando...");
            }
        }

        return charged;
    }

    private void callThirdPartyCardNetwork(String cardNumber, double amount) throws SocketTimeoutException {
        // Simula fallo de timeout recurrente
        throw new SocketTimeoutException("Read timed out from external visa/mastercard processor");
    }
}
