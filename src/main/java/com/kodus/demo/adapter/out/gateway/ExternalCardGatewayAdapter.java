package com.kodus.demo.adapter.out.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

/**
 * Caso 5: Resiliencia y Estabilidad - Bucle Infinito de Reintentos (Cascading Failure).
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Bucle while (true) sin condición de parada, sin límite máximo de intentos (unbounded retries).
 * 2. Ausencia de retroceso exponencial (exponential backoff) ni Circuit Breaker.
 * 3. Provoca agotamiento de hilos del servidor web (Thread Pool Starvation) y consumo del 100% de CPU
 *    ante caídas del proveedor externo, tirando abajo la aplicación completa.
 */
@Component
public class ExternalCardGatewayAdapter {

    private static final Logger log = LoggerFactory.getLogger(ExternalCardGatewayAdapter.class);

    public boolean chargeExternalCard(String cardNumber, double amount) {
        log.info("Iniciando cobro a tarjeta {} por monto {}", cardNumber, amount);

        boolean charged = false;

        // ❌ VIOLACIÓN CRÍTICA: Bucle infinito de reintentos sin backoff, sin timeout y sin max attempts
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
