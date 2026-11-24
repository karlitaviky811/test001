---
title: "Regla 10: Persistencia y Seguridad - Prohibición de Entidades JPA en API y SQL Concatenado"
scope: "file"
path: ["src/main/java/**/adapter/in/web/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Evitar la fuga de modelos internos de base de datos hacia la API pública y prevenir vulnerabilidades de inyección SQL (SQL Injection).

## Instrucciones para Kodus
1. **Prohibición de Entidades JPA en Controladores**: Los controladores web (`@RestController`) no deben retornar ni recibir clases marcadas con `@Entity`. Exige el uso exclusivo de Data Transfer Objects (DTOs).
2. **Prohibición de SQL Concatenado**: Prohíbe ejecutar sentencias SQL nativas concatenando parámetros de entrada del usuario con `JdbcTemplate` o EntityManager. Toda consulta debe estar parametrizada en la capa de persistencia (`adapter.out.persistence`).
