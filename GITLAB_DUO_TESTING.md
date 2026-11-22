# Guía de Prueba: GitLab Duo Code Review en Merge Requests

Esta guía explica cómo configurar, disparar y verificar que **GitLab Duo Code Review** evalúe las reglas de negocio, arquitectura y prevención de deuda técnica definidas para este repositorio.

---

## 1. ¿Cómo funciona GitLab Duo Code Review con Reglas Personalizadas?

GitLab Duo soporta instrucciones a nivel de repositorio mediante el archivo:
📄 [.gitlab/duo/mr-review-instructions.yaml](file:///C:/Users/ktovar/kodus-java-backend-demo/.gitlab/duo/mr-review-instructions.yaml)

Cuando se crea o actualiza un Merge Request en GitLab:
1. GitLab Duo escanea este archivo en la rama por defecto (`main` o `master`).
2. Aplica los filtros de archivo (`fileFilters`) a los archivos cambiados en el MR.
3. Inyecta tus instrucciones como contexto adicional a su modelo de revisión.
4. Genera comentarios en línea (inline comments) o un resumen de revisión en el MR.

---

## 2. Cómo disparar la revisión de GitLab Duo en un Merge Request

En GitLab, puedes solicitar que Duo revise tu Merge Request de tres formas:

### Opción A: Quick Action en un comentario del MR (La más rápida)
En la caja de comentarios del Merge Request, escribe:
```text
/assign_reviewer @GitLabDuo
```
*(O también `/request_review @GitLabDuo`)*

### Opción B: Mencionar al Bot
Escribe un comentario en la discusión del MR:
```text
@GitLabDuo please review this merge request
```

### Opción C: Automática (Configuración de Proyecto)
Si eres Owner o Maintainer del proyecto en GitLab:
* Ve a **Settings > General > Merge requests**.
* Activa la casilla **GitLab Duo Code Review** (ejecutar automáticamente al abrir o actualizar el MR).

---

## 3. Escenario de Prueba para GitLab Duo (Caso de Prueba Deliberado)

Para comprobar que GitLab Duo realmente aplica las reglas configuradas en `.gitlab/duo/mr-review-instructions.yaml`:

### Paso 1: Crear una rama de prueba
```bash
cd C:\Users\ktovar\kodus-java-backend-demo
git checkout -b test/gitlab-duo-review-demo
```

### Paso 2: Introducir el anti-patrón de prueba
Agrega un método en `PaymentController.java` que viole deliberadamente las reglas:

```java
// Archivo: src/main/java/com/kodus/demo/adapter/in/web/PaymentController.java

@PostMapping("/quick-transfer-debt")
public com.kodus.demo.adapter.out.persistence.PaymentJpaEntity transferConDeuda(
        @RequestBody com.kodus.demo.adapter.out.persistence.PaymentJpaEntity entity) {
    
    // ❌ Violación Regla 2: Uso de double para comisiones financieras
    double fee = entity.getAmount().doubleValue() * 0.02;
    
    // ❌ Violación Regla 4: Lógica de negocio e invariantes en el Controller
    if (fee > 50.0) {
        entity.setStatus("REJECTED");
    }
    
    // ❌ Violación Regla 3: Retornar @Entity directamente desde el endpoint REST
    return entity;
}
```

### Paso 3: Hacer commit, push y abrir el Merge Request en GitLab
```bash
git add .
git commit -m "test: introduce deliberate architectural anti-patterns for GitLab Duo review"
git push -u origin test/gitlab-duo-review-demo
```

### Paso 4: Solicitar la revisión a GitLab Duo
En el Merge Request recién creado, publica un comentario con:
```text
/assign_reviewer @GitLabDuo
```

---

## 4. Qué comentarios esperar de GitLab Duo

GitLab Duo analizará el diff del MR contra `.gitlab/duo/mr-review-instructions.yaml` y generará comentarios señalando:

1. **Sobre el tipo `double fee`**:
   > *"Violación de la regla 'Precisión Financiera': Se detectó el uso de tipo primitivo `double` para el cálculo de `fee`. Debe usarse el Value Object `Money` o `BigDecimal` con `RoundingMode.HALF_EVEN` para evitar errores de precisión binaria (IEEE 754)."*

2. **Sobre el uso de `PaymentJpaEntity` en el endpoint**:
   > *"Violación de la regla 'Prevención de Deuda Técnica': El endpoint `@PostMapping("/quick-transfer-debt")` expone y recibe directamente una entidad JPA (`PaymentJpaEntity`). Utiliza DTOs (`PaymentRequestDto` y `PaymentResponseDto`) para evitar acoplamiento con la base de datos."*

3. **Sobre la condición `if (fee > 50.0) entity.setStatus(...)`**:
   > *"Violación de la regla 'Regla de Negocio - Transiciones de Estado': La lógica de validación de negocio y cambios de estado no debe residir en el `@RestController`. Debe delegarse al agregado `PaymentOrder` en el dominio."*

---

## 5. Comparativa: Kodus vs GitLab Duo Code Review

| Característica | Kodus (Self-Hosted) | GitLab Duo Code Review |
| :--- | :--- | :--- |
| **Ubicación de Reglas** | `.kody/rules/*.md` + CLI + `CLAUDE.md` | `.gitlab/duo/mr-review-instructions.yaml` |
| **Gobernanza** | Policy as Code + Centralized Config | Repositorio o Group-level instructions |
| **Disparo** | Webhook automático o CLI (`kodus review`) | Quick action `/assign_reviewer @GitLabDuo` o auto |
| **Memoria / Aprendizaje** | Registra "Memories" interactivas desde los comentarios del PR | Contexto fijo inyectado en el prompt por MR |
| **Tracking de Deuda Técnica** | Convierte comentarios en métricas de Cockpit e issues automáticos | Comentarios de revisión en el MR |
