---
title: "Prevención de Deuda Técnica - Prohibición de Entidades JPA en Controladores"
scope: "file"
path: ["src/main/java/**/adapter/in/web/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Evitar la fuga del modelo de base de datos hacia la API pública (acoplamiento y fallos de seguridad como Mass Assignment o LazyInitializationException).

## Instrucciones para Kodus
1. Examina las clases con `@RestController` o `@Controller` dentro de los adaptadores web.
2. **PROHIBIDO**: 
   - Métodos de endpoint (`@GetMapping`, `@PostMapping`, etc.) que retornen directamente clases marcadas con `@Entity`.
   - Métodos que acepten entidades `@Entity` como `@RequestBody`.
3. Si detectas una entidad JPA expuesta en el controlador:
   - Emite una alerta de severidad **HIGH** por Deuda Técnica de Acoplamiento.
   - Solicita el uso de Data Transfer Objects (DTOs) específicos (ej. `PaymentRequestDto`, `PaymentResponseDto`).
   - Recomienda mapear entre DTOs y Comandos/Modelos mediante métodos de factoría estática o mappers dedicados.
