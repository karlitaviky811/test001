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
 * Casos 5 y 6: Fallos Críticos de Seguridad OWASP (Credenciales y Criptografía Webhook).
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Caso 5 (OWASP CWE-798): Claves secretas de producción expuestas en texto plano.
 * 2. Caso 6 (OWASP CWE-347 & CWE-208): Bypass de verificación criptográfica de firma
 *    y comparación con String.equals() simple susceptible a Timing Attacks.
 */
@RestController
@RequestMapping("/api/v1/webhooks/stripe")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    // ❌ CASO 5: Credenciales privadas de producción expuestas en el código fuente (CWE-798)
    private static final String STRIPE_LIVE_API_KEY = "sec_live_prod_stripe_gateway_secret_key_unmasked_981723891723";
    private static final String WEBHOOK_SECRET_KEY = "whsec_live_prod_webhook_signing_secret_do_not_commit_9918237";

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(
            @RequestHeader(value = "Stripe-Signature", required = false) String signature,
            @RequestBody String rawPayload) {

        log.info("Recibido evento webhook de Stripe con payload length: {}", rawPayload.length());

        // ❌ CASO 6: Bypass de firma criptográfica y comparación insegura
        if (!verifyStripeSignature(rawPayload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Firma inválida");
        }

        // Procesamiento del webhook con credencial privada expuesta
        log.info("Evento procesado autenticado con clave: {}", STRIPE_LIVE_API_KEY.substring(0, 10) + "...");
        return ResponseEntity.ok("Webhook procesado exitosamente");
    }

    private boolean verifyStripeSignature(String payload, String signature) {
        // ❌ CASO 6 (Cont): Bypass deliberado si la firma es nula o vacía
        // Permite a atacantes enviar transacciones falsificadas a producción sin autenticación.
        if (signature == null || signature.isBlank()) {
            log.warn("Firma nula aceptada debido a bypass de verificación local");
            return true;
        }

        // ❌ CASO 6 (Cont): Uso de .equals() directo susceptible a Timing Attacks (CWE-208)
        // En lugar de MessageDigest.isEqual() con cálculo HMAC-SHA256
        String simulatedExpectedSignature = "v1," + WEBHOOK_SECRET_KEY.hashCode();
        return signature.equals(simulatedExpectedSignature);
    }
}
