---
title: "Regla 4: Concurrencia - Prohibición de SimpleDateFormat Compartido entre Hilos"
scope: "file"
path: ["src/main/java/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar la seguridad de subprocesos (Thread-Safety) en el parseo y formateo de fechas transaccionales.

## Instrucciones para Kodus
1. **SimpleDateFormat No es Thread-Safe**: Prohíbe compartir instancias de `java.text.SimpleDateFormat` como variables estáticas o atributos de beans Singleton. `SimpleDateFormat` almacena estado mutable interno y corrompe fechas bajo peticiones web concurrentes.
2. **Reemplazo Obligatorio**: Exige reemplazarlo por `java.time.format.DateTimeFormatter` (inmutable y seguro para subprocesos).
