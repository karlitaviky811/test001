---
title: "Deuda Técnica y Concurrencia - Prohibición de Estado Estático Mutable en Spring Singletons"
scope: "file"
path: ["src/main/java/**"]
severity: "high"
languages: ["java"]
enabled: true
---

## Objetivo
Garantizar la seguridad de subprocesos (Thread-Safety), prevenir condiciones de carrera (Race Conditions) y evitar fugas de memoria (Memory Leaks) en la arquitectura web multihilo de Spring Boot.

## Instrucciones para Kodus
1. **Colecciones Estáticas Mutables Prohibidas**: Prohíbe el uso de colecciones estáticas no sincronizadas (ej. `private static final Map<...> = new HashMap<>()` o `HashSet`) dentro de beans de Spring (`@Component`, `@Service`, `@RestController`). En un entorno multihilo generan corrupción de datos y fuga de memoria por falta de políticas de desalojo (TTL/LRU).
2. **SimpleDateFormat Prohibido**: Prohíbe compartir instancias de `java.text.SimpleDateFormat` como constantes estáticas o atributos de beans Singleton. `SimpleDateFormat` no es thread-safe y corrompe fechas en concurrencia. Exige `java.time.format.DateTimeFormatter` (inmutable y seguro).
