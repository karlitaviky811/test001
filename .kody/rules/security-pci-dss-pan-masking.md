---
title: "Regla 9: Seguridad y PCI-DSS - Prohibición de Números de Tarjeta (PAN) en Texto Plano en Logs"
scope: "file"
path: ["src/main/java/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Cumplir con el estándar normativo PCI-DSS y proteger la información personal identificable (PII) financiera de los titulares de tarjeta.

## Instrucciones para Kodus
1. **Prohibición de PAN en Logs**: Prohíbe imprimir números completos de tarjeta de crédito (PAN de 16 dígitos) o códigos de seguridad (CVV) en texto claro en logs (`log.info`, `log.warn`) o salida estándar (`System.out`).
2. **Enmascaramiento Obligatorio**: Todo dato de tarjeta debe ser ofuscado mostrando únicamente los últimos 4 dígitos (ej. `****-****-****-1234`).
