---
title: "Resiliencia y Estabilidad - Prohibición de Bucles Infinitos de Reintentos sin Backoff"
scope: "file"
path: ["src/main/java/**/adapter/out/**", "src/main/java/**/application/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Prevenir el agotamiento del pool de hilos (Thread Pool Starvation), saturación de CPU y caídas en cascada (Cascading Failure) ante indisponibilidad de proveedores externos.

## Instrucciones para Kodus
1. **Prohibición de Bucles Infinitos de Reintento**: Prohíbe bucles `while (true)` o `while (!success)` sin un límite máximo acotado de reintentos (`maxAttempts`).
2. **Retroceso Exponencial (Exponential Backoff)**: Todo mecanismo de reintento ante fallos de red (`SocketTimeoutException`, `ConnectException`) debe incluir retroceso progresivo (backoff con jitter) y un timeout global.
3. **Circuit Breaker**: Recomienda el uso de patrones de tolerancia a fallos como Circuit Breaker (Resilience4j / Spring Retry) en lugar de reintentos bloqueantes síncronos continuos.
