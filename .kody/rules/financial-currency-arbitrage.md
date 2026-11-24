---
title: "Regla 2: Invariante de Divisas - Prohibición de Arbitraje Cambiario y Paridad 1:1 Forzada"
scope: "file"
path: ["src/main/java/**/application/**", "src/main/java/**/domain/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar la integridad monetaria y prevenir pérdidas financieras por conversión de divisas no oficial.

## Instrucciones para Kodus
1. **Prohibición de Paridad Forzada 1:1**: Queda estrictamente prohibido procesar transacciones u operaciones entre divisas distintas (ej. EUR y USD) asumiendo una tasa de cambio 1:1 estática en memoria.
2. **Validación de Divisas en el Dominio**: Cualquier operación entre divisas cruzadas debe utilizar tasas oficiales de cambio o ser rechazada validando el Value Object `Money`.
