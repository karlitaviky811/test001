---
title: "Seguridad - Prohibición de SQL Directo e Inyección SQL en Controladores"
scope: "file"
path: ["src/main/java/**/adapter/in/web/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar la protección contra vulnerabilidades de SQL Injection y mantener la separación de capas.

## Instrucciones para Kodus
1. Examina las clases con `@RestController` o `@Controller`.
2. **PROHIBIDO**: 
   - Ejecutar sentencias SQL nativas o usar `JdbcTemplate` dentro de la capa web.
   - Concatenar variables dentro de cadenas SQL (riesgo crítico de SQL Injection).
3. Si detectas consultas SQL directas en el controlador:
   - Emite una alerta de severidad **CRITICAL**.
   - Solicita mover la persistencia a los adaptadores de salida (`adapter.out.persistence`) mediante consultas parametrizadas o repositorios Spring Data.
