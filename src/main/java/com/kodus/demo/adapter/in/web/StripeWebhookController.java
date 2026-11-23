package com.kodus.demo.adapter.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Caso 3: Fallos Críticos de Seguridad OWASP.
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Hardcoded Production Credentials (CWE-798): Claves secretas de producción
 *    expuestas en texto claro en el código fuente.
 * 2. Bypass de Verificación Criptográfica de Webhook (CWE-347): Acepta firmas nulas
 *    o utiliza comparaciones inseguras susceptibles a ataques de temporización (Timing Attacks).
 */
@RestController
@RequestMapping("/api/v1/webhooks/stripe")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    // ❌ VIOLACIÓN 1: Credencial privada de producción expuesta en el código fuente (CWE-798)
    private static final String STRIPE_LIVE_API_KEY = "sec_live_prod_stripe_gateway_secret_key_unmasked_981723891723";
    private static final String WEBHOOK_SECRET_KEY = "whsec_live_prod_webhook_signing_secret_do_not_commit_9918237";

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(
            @RequestHeader(value = "Stripe-Signature", required = false) String signature,
            @RequestBody String rawPayload) {

        log.info("Recibido evento webhook de Stripe con payload length: {}", rawPayload.length());

        // ❌ VIOLACIÓN 2: Bypass de firma criptográfica y comparación insegura
        if (!verifyStripeSignature(rawPayload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Firma inválida");
        }

        // Procesamiento del webhook con credencial privada expuesta
        log.info("Evento procesado autenticado con clave: {}", STRIPE_LIVE_API_KEY.substring(0, 10) + "...");
        return ResponseEntity.ok("Webhook procesado exitosamente");
    }

    private boolean verifyStripeSignature(String payload, String signature) {
        // ❌ Bypass deliberado: Si la firma es nula o vacía, se asume válida en modo de prueba/desarrollo
        // Esto permite a cualquier atacante enviar eventos falsificados a producción sin autenticación.
        if (signature == null || signature.isBlank()) {
            log.warn("Firma nula aceptada debido a bypass de verificación local");
            return true;
        }

        // ❌ Uso de .equals() directo susceptible a Timing Attacks (CWE-208)
        // En lugar de MessageDigest.isEqual() con cálculo HMAC-SHA256
        String simulatedExpectedSignature = "v1," + WEBHOOK_SECRET_KEY.hashCode();
        return signature.equals(simulatedExpectedSignature);
    }
}
