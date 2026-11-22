---
title: "Manejo de Errores - Prohibición de catch genérico y silenciamiento"
scope: "file"
path: ["src/main/java/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Evitar el silenciamiento de fallos y la pérdida de stacktraces en producción.

## Instrucciones para Kodus
1. Examina bloques `try-catch`.
2. **PROHIBIDO**: 
   - `catch (Exception e)` o `catch (Throwable t)` sin relanzar una excepción de dominio.
   - Retornar `null` al capturar un fallo.
   - Usar `e.printStackTrace()` o `System.out.println()`.
3. Si detectas silenciamiento de excepciones:
   - Emite una alerta de severidad **HIGH**.
   - Exige logging estructurado (`log.error(...)`) y propagación de la excepción.
