---
title: "Precisión Financiera - Prohibición de float y double"
scope: "file"
path: ["src/main/java/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Evitar errores de redondeo de punto flotante binario (IEEE 754) en operaciones financieras y de dinero.

## Instrucciones para Kodus
1. Analiza cualquier campo, parámetro o variable que represente montos monetarios, comisiones, tarifas o saldos.
2. **PROHIBIDO**: El uso de tipos primitivos `float`, `double` o sus wrappers `Float`, `Double` para cálculos o almacenamiento de dinero.
3. Si encuentras un `double` o `float` representando dinero:
   - Emite una alerta de severidad **HIGH**.
   - Cita el riesgo: *"Los tipos de punto flotante binario introducen inexactitudes críticas en cálculos monetarios (ej. 0.1 + 0.2 != 0.3)"*.
   - Sugiere reemplazarlo por el Value Object `com.kodus.demo.domain.model.Money` o `java.math.BigDecimal` con `RoundingMode.HALF_EVEN` y escala explícita.
