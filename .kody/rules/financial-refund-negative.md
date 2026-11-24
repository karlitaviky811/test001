---
title: "Regla 1: Invariante Financiero - Prohibición de Reembolsos Negativos y Límite Excedido"
scope: "file"
path: ["src/main/java/**/application/**", "src/main/java/**/adapter/in/web/**", "src/main/java/**/domain/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Prevenir fraude de inyección de saldo y manipulación de balances contables en operaciones de reembolso.

## Instrucciones para Kodus
1. **Montos Negativos Estrictamente Prohibidos**: Todo reembolso debe validar obligatoriamente que el importe sea estrictamente positivo (`amount.compareTo(BigDecimal.ZERO) > 0`). Si el código permite números negativos o menores/iguales a cero, márcalo como VULNERABILIDAD CRÍTICA DE LÓGICA FINANCIERA (Fraude de Inyección de Saldo).
2. **Límite Máximo de Reembolso**: Debe validarse que el monto total acumulado del reembolso no exceda el importe original pagado en la orden.
