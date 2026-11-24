---
title: "Regla 5: Seguridad OWASP CWE-798 - Prohibición de Secretos de Producción en Código Duro"
scope: "file"
path: ["src/main/java/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Evitar la fuga de credenciales sensibles, claves de pasarela y tokens criptográficos en el repositorio.

## Instrucciones para Kodus
1. **Prohibición de Claves en Texto Plano**: Detecta y rechaza cualquier constante o literal que contenga claves privadas de API (ej. `sec_live_...`, tokens de servicio o claves de pasarela). Exige cargarlas desde variables de entorno o almacén seguro (`@Value("${...}")`).
