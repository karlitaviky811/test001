# Demostración Paso a Paso: Detección de Reglas de Negocio, Aprendizaje y Deuda Técnica con Kodus

Esta guía interactiva contiene el guion para demostrar cómo Kodus actúa como el guardián de calidad en tu equipo de ingeniería.

---

## Escenario 1: Código Limpio que Cumple las Políticas

1. Ejecuta las pruebas unitarias y de arquitectura del backend:
   ```bash
   mvn clean test
   ```
   *Resultado*: Todas las pruebas pasan (precisión de `Money`, validación de saldos en `PaymentOrder`, idempotencia en `ProcessPaymentService` y DTOs en `PaymentController`).

2. Ejecuta la revisión de Kodus:
   ```bash
   kodus review
   ```
   *Resultado*: Kodus reporta `0 violaciones`. La arquitectura hexagonal y las reglas declaradas en `.kody/rules/` se cumplen íntegramente.

---

## Escenario 2: Detección Automática de Reglas de Negocio y Deuda Técnica

Simula a un desarrollador junior o con prisas que comete 3 faltas típicas de deuda técnica:

### El Código con Mala Práctica (Anti-patrón):
Imagina un nuevo commit en una rama `feature/quick-fee-discount` donde se modifica el controlador o un nuevo servicio:

```java
// ❌ ERROR 1 (Deuda Técnica): Exponer la entidad JPA en el Controller
@PostMapping("/quick-transfer")
public PaymentJpaEntity transferDirecta(@RequestBody PaymentJpaEntity entity) { // Violación de regla no-orm-entities-in-controllers
    
    // ❌ ERROR 2 (Violación de Regla de Negocio Financiera): Usar double para comisiones
    double fee = entity.getAmount().doubleValue() * 0.015; // Violación de regla financial-precision-money
    
    // ❌ ERROR 3 (Violación de Dominio): Lógica de estado hecha en el Controller sin pasar por la entidad
    if (entity.getAmount().doubleValue() > 1000.0) {
        entity.setStatus("REJECTED"); // Bypasea PaymentOrder.authorize()
    }
    return repository.save(entity);
}
```

### La Intervención de Kodus en el Pull Request:
Al abrir el PR o ejecutar `kodus review`:
1. **Comentario 1 (High)**: 
   > *"Flagged by `no-orm-entities-in-controllers`: El método `transferDirecta` recibe y expone `PaymentJpaEntity`. Esto genera acoplamiento entre la API externa y el esquema de base de datos. Utiliza un `PaymentRequestDto` y un `PaymentResponseDto`."*
2. **Comentario 2 (High)**:
   > *"Flagged by `financial-precision-money`: Se detectó cálculo de comisión con tipo primitivo `double` (`0.015`). El punto flotante binario genera discrepancias de redondeo en transacciones financieras. Utiliza el Value Object `Money` o `BigDecimal` con `RoundingMode.HALF_EVEN`."*
3. **Comentario 3 (Critical / Business Invariant)**:
   > *"Flagged by `business-invariant-limits`: Se detectó mutación de estado directa (`setStatus('REJECTED')`) en la capa web. Las transiciones de estado deben ser orquestadas por el agregado de dominio `PaymentOrder` mediante `order.authorize(...)` o `order.reject(...)`."*

---

## Escenario 3: Cómo Kodus "Aprende" (Memoria Contextual y Acuerdos de Equipo)

Una de las capacidades más potentes de Kodus self-hosted es que **no es un linter rígido, sino un agente con memoria contextual**.

### Flujo de Aprendizaje:
1. Supongamos que el equipo introduce una excepción intencional aprobada por arquitectura:
   * *Caso*: *"Para transferencias internas entre cuentas maestras del banco (`ACC-TREASURY`), se permite omitir la validación de límite diario"*.
2. En el hilo de discusión del Pull Request en GitHub/GitLab, el desarrollador responde al comentario de Kodus:
   > *"@kodus esta excepción es válida según la especificación de Tesorería RFC-402: las cuentas que comiencen con `ACC-TREASURY` no están sujetas a límite diario. Guarda esta regla en memoria."*
3. Kodus procesa la retroalimentación, registra la **Memoria Contextual** en su base de datos local y responde confirmando que en futuros PRs no marcará falsos positivos cuando la cuenta origen sea de tesorería.

---

## Escenario 4: Resolución Progresiva de Deuda Técnica (Technical Debt Tracking)

1. **Visibilidad en el Cockpit**:
   Kodus registra las recomendaciones no atendidas en su panel de métricas (`kodus cockpit`), permitiendo al Lead Architect visualizar:
   * Ratio de deuda técnica por componente.
   * Archivos con mayor propensión a fugas de arquitectura.
2. **Generación Automática de Tareas de Refactor**:
   Kodus puede vincularse con Jira/Linear para convertir comentarios de deuda técnica aceptados temporalmente en tickets técnicos del backlog (`Tech-Debt: Refactor legacy controller to DTO pattern`).
3. **Validación de Cierre**:
   Cuando el desarrollador envía el PR de refactorización corrigiendo el código hacia Arquitectura Hexagonal, ejecuta:
   ```bash
   kodus pr business-validation
   ```
   Kodus valida que los criterios de aceptación y las políticas se cumplen al 100%, dando el aprobado final al merge.
