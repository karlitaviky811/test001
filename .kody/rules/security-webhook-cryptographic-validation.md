---
title: "Regla 6: Seguridad OWASP CWE-347 & CWE-208 - Validación Criptográfica de Webhooks y Timing Attacks"
scope: "file"
path: ["src/main/java/**/adapter/in/web/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar la autenticación de eventos de pago y evitar ataques de falsificación de webhooks y temporización.

## Instrucciones para Kodus
1. **Prohibición de Bypass de Firma**: En controladores que reciban webhooks, prohíbe aceptar firmas nulas, vacías o simular validaciones retornando `true`.
2. **Prevención de Timing Attacks (CWE-208)**: La comparación de firmas criptográficas HMAC debe realizarse en tiempo constante usando `MessageDigest.isEqual()` y nunca `String.equals()`.
