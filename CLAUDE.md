# Directrices de Arquitectura y Revisión de Código para Kodus

Este repositorio implementa un núcleo de procesamiento de pagos y transferencias diseñado bajo **Arquitectura Hexagonal (Ports & Adapters)** y **Domain-Driven Design (DDD)**.

## 1. Principios de Arquitectura Innegociables

- **Dominio Puro (`domain`)**:
  - No contiene dependencias externas. Cero imports de Spring, JPA, Hibernate o JSON annotations.
  - La lógica de negocio y las invariantes viven en las entidades y Value Objects.
  - El dinero siempre se modela con el Value Object `Money` (respaldado por `BigDecimal`). Prohibido `double` o `float`.
- **Capa de Aplicación (`application`)**:
  - Define casos de uso (`usecase`) y puertos (`port.in`, `port.out`).
  - Orquesta las transacciones y coordina la persistencia y auditoría a través de interfaces de puertos.
- **Capa de Adaptadores (`adapter`)**:
  - `adapter.in.web`: Controladores REST. Solo manejan DTOs y códigos de estado HTTP. Jamás exponen entidades `@Entity`.
  - `adapter.out.persistence`: Implementaciones de puertos de persistencia con Spring Data JPA. Mapea entre `PaymentOrder` (dominio) y `PaymentJpaEntity` (tabla).

## 2. Detección de Deuda Técnica

Kodus debe marcar como deuda técnica prioritaria:
1. Setter injection o campos mutables en entidades de dominio.
2. Falta de validación de unicidad o idempotencia en transacciones financieras.
3. Controladores que ejecutan lógica de negocio o cálculos matemáticos.
4. Omisión de pruebas unitarias sobre transiciones de estado en entidades de dominio.

## 3. Convenciones de Equipo y Aprendizaje

- Cuando un desarrollador documente una excepción legítima en un PR, Kodus debe registrar la memoria contextual correspondiente para no bloquear evoluciones de negocio aprobadas por el equipo.
