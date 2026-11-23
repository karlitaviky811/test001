---
title: "Seguridad OWASP - Prohibición de Credenciales en Código y Bypass Criptográfico"
scope: "file"
path: ["src/main/java/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Cumplir con las directrices OWASP (CWE-798 y CWE-347) evitando la exposición de secretos en repositorios y garantizando la integridad criptográfica de webhooks de pago.

## Instrucciones para Kodus
1. **Prohibición de Secretos en Texto Plano**: Detecta y rechaza cualquier clave de API privada (ej. `sk_live_...`, tokens, contraseñas o secretos de firma `whsec_...`) hardcodeada en el código fuente. Exige su lectura a través de variables de entorno seguras o vault externo (`@Value("${...}")`).
2. **Prohibición de Bypass de Firmas**: En controladores o adaptadores de webhook, prohíbe admitir firmas nulas, vacías o simular validaciones con retornos `true` incondicionales.
3. **Prevención de Timing Attacks**: Exige que la validación de hashes criptográficos HMAC-SHA256 use comparaciones en tiempo constante (ej. `MessageDigest.isEqual()`) en lugar de `String.equals()`.
