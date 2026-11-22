---
title: "Aislamiento de Dominio - Arquitectura Hexagonal"
scope: "file"
path: ["src/main/java/**/domain/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar que el núcleo del negocio (`domain`) permanezca completamente aislado de frameworks externos, tecnologías de persistencia y protocolos de transporte.

## Instrucciones para Kodus
1. Revisa todas las clases e interfaces dentro del paquete `domain`.
2. **PROHIBIDO**: Cualquier importación perteneciente a frameworks:
   - `org.springframework.*` (ej. `@Service`, `@Component`, `@Autowired`, `@Transactional`)
   - `jakarta.persistence.*` o `javax.persistence.*` (ej. `@Entity`, `@Table`, `@Id`)
   - `jakarta.servlet.*` o `org.springframework.web.*`
   - Clientes de base de datos, Kafka o HTTP.
3. Si detectas alguna de estas dependencias dentro de `domain`:
   - Clasifica el hallazgo como **CRITICAL**.
   - Explica que el dominio debe depender únicamente del estándar de Java.
   - Recomienda mover la anotación o tecnología al adaptador correspondiente (`adapter.in`, `adapter.out`) o a la capa de aplicación (`application.service`).
