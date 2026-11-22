---
title: "Regla de Negocio - Validación de Límites y Transiciones de Estado"
scope: "file"
path: ["src/main/java/**/domain/**", "src/main/java/**/application/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Asegurar que las reglas de negocio e invariantes (límites diarios, estados de pago válidos) residan exclusivamente en el Dominio y no se dispersen en controladores o servicios anémicos.

## Instrucciones para Kodus
1. Comprueba que las mutaciones de estado de `PaymentOrder` (como `authorize()`, `settle()`, `reject()`) se realicen a través de métodos de negocio dentro de la entidad y no con setters abiertos.
2. Verifica que las transiciones de estado invaliden estados terminales (ej. un pago `REJECTED` no puede pasar a `SETTLED`).
3. Si detectas lógica condicional de negocio (`if (status == ...)` o cálculo de límites) colocada en un `@RestController`, marca el hallazgo indicando que viola la cohesión de dominio y debe moverse a la capa de dominio o al caso de uso.
