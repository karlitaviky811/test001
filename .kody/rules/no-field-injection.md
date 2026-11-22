---
title: "Prohibición de Field Injection (@Autowired en atributos)"
scope: "file"
path: ["src/main/java/**"]
severity: "medium"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar inmutabilidad y testabilidad mediante inyección por constructor.

## Instrucciones para Kodus
1. Examina las clases de componentes, servicios y adaptadores.
2. **PROHIBIDO**: El uso de `@Autowired` sobre atributos privados.
3. Si detectas `@Autowired private ...`:
   - Emite una alerta de severidad **MEDIUM**.
   - Solicita inyección por constructor.
