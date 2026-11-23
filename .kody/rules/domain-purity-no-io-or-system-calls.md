---
title: "Aislamiento Hexagonal Puro - Prohibición de I/O de Red y Llamadas al Sistema en Dominio"
scope: "file"
path: ["src/main/java/**/domain/**"]
severity: "critical"
languages: ["java"]
enabled: true
---

## Objetivo
Mantener el núcleo de Dominio (Domain Core) como Java puro y determinista, libre de efectos secundarios de red, I/O o dependencias del entorno operativo.

## Instrucciones para Kodus
1. **Prohibición de Clientes HTTP / Red en Dominio**: Prohíbe la instanciación o invocación directa de clientes de red como `java.net.http.HttpClient`, `HttpURLConnection`, `RestTemplate` o sockets dentro de entidades, agregados o value objects en `com.kodus.demo.domain.*`.
2. **Prohibición de Variables de Entorno en Dominio**: Prohíbe invocar `System.getenv(...)` o `System.getProperty(...)` dentro del dominio.
3. **Inversión de Dependencias (Puertos y Adaptadores)**: Si el dominio necesita desencadenar una notificación externa, debe emitir un evento de dominio (`Domain Event`) o declarar un Puerto de Salida (`Port Out`) para que la infraestructura se encargue del envío.
