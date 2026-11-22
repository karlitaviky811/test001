---
title: "Seguridad y Auditoría Financiera - Prohibición de PII en logs"
scope: "file"
path: ["src/main/java/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Evitar fugas de datos confidenciales en logs y garantizar la trazabilidad contable.

## Instrucciones para Kodus
1. Verifica mensajes de log (`log.info`, `log.debug`, etc.) o salida estándar.
2. **PROHIBIDO**: Volcar tokens de autorización, claves secretas o números completos de cuenta sin enmascarar.
3. Verifica que los flujos de cobro/transferencia invoquen el puerto de auditoría `AuditEventPublisherPort`.
