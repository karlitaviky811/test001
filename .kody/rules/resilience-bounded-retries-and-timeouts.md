---
title: "Regla 8: Resiliencia - Prohibición de Bucles Infinitos de Reintento sin Backoff (Thread Starvation)"
scope: "file"
path: ["src/main/java/**/adapter/out/**", "src/main/java/**/application/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Prevenir la saturación de CPU, el agotamiento del pool de hilos de Tomcat/Undertow y caídas en cascada ante indisponibilidad de terceros.

## Instrucciones para Kodus
1. **Prohibición de Bucle Infinito**: Prohíbe bucles `while (true)` o `while (!success)` sin límite superior de reintentos (`maxAttempts`).
2. **Retroceso y Timeout Obligatorios**: Todo reintento ante fallos de red debe incorporar retroceso exponencial (backoff), jitter, timeout explícito o patrones de Circuit Breaker.
