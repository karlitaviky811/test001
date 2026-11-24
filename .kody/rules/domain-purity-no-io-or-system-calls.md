---
title: "Regla 7: Arquitectura Hexagonal y DDD - Prohibición de I/O de Red y System.getenv en Dominio"
scope: "file"
path: ["src/main/java/**/domain/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Preservar la pureza del núcleo de Dominio, manteniéndolo determinista, libre de dependencias de infraestructura y testeable sin red.

## Instrucciones para Kodus
1. **Prohibición de Clientes HTTP en Dominio**: Prohíbe instanciar o usar `java.net.http.HttpClient`, `RestTemplate` o sockets dentro del paquete `domain`.
2. **Prohibición de Variables del Sistema**: Prohíbe invocar `System.getenv()` o `System.getProperty()` dentro de entidades, agregados o value objects. Toda comunicación con el exterior debe abstraerse mediante un Puerto de Salida (`Port Out`).
