---
title: "Regla de Negocio Financiera - Invariantes de Reembolso y Arbitraje de Divisas"
scope: "file"
path: ["src/main/java/**/application/**", "src/main/java/**/adapter/in/web/**", "src/main/java/**/domain/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Prevenir fraude financiero, manipulación indebida de saldos y arbitraje no autorizado de divisas en operaciones de reembolso.

## Instrucciones para Kodus
1. **Montos Negativos Estrictamente Prohibidos**: Todo reembolso debe validar obligatoriamente que el monto sea estrictamente positivo (`amount.compareTo(BigDecimal.ZERO) > 0` o `amount.isPositive()`). Si se permite un monto negativo (o `<= 0`), márcalo como VULNERABILIDAD CRÍTICA DE LÓGICA FINANCIERA (Fraude de Inyección de Saldo).
2. **Arbitraje y Cruce de Divisas Prohibido**: Un reembolso no puede procesarse forzando una paridad 1:1 estática entre monedas distintas (ej. EUR vs USD). Las conversiones cambiarias deben realizarse a través de servicios de tasa de cambio oficiales o validarse con el Value Object `Money`.
3. **Límite de Reembolso**: Debe comprobarse que el monto acumulado del reembolso no exceda el importe original de la orden de pago.
