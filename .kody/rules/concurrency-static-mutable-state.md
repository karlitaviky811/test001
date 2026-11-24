---
title: "Regla 3: Concurrencia en Spring - Prohibición de Estado Estático Mutable en Singletons"
scope: "file"
path: ["src/main/java/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Prevenir condiciones de carrera (Race Conditions), corrupción de datos y fugas de memoria (Memory Leaks) en Beans Singleton de Spring Boot.

## Instrucciones para Kodus
1. **Colecciones Estáticas Mutables Prohibidas**: Prohíbe declarar colecciones estáticas mutables no sincronizadas (ej. `private static final Map<...> = new HashMap<>()` o `HashSet`) en clases gestionadas por Spring (`@Component`, `@Service`, `@RestController`).
2. **Fuga de Memoria (Memory Leak)**: Cualquier mapa o caché en memoria debe contar con política de desalojo (TTL, LRU o tamaño máximo acotado).
