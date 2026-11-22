# Kodus & GitLab Duo Java Backend Demo: Core de Pagos con Arquitectura Hexagonal

Backend de demostración en **Java 21** y **Spring Boot 3.3.5** diseñado específicamente para ilustrar cómo herramientas de AI Code Review como **Kodus (Self-Hosted)** y **GitLab Duo Code Review** detectan violaciones de reglas de negocio, aprenden de las decisiones del equipo y guían la eliminación progresiva de deuda técnica.

---

## 1. Fundamentos de Arquitectura

El proyecto sigue una estricta **Arquitectura Hexagonal (Ports and Adapters)** combinada con **Domain-Driven Design (DDD)** táctico:

```
src/main/java/com/kodus/demo/
├── domain/                    # Núcleo Puro: Entidades, Value Objects e Invariantes
│   ├── model/                 # PaymentOrder, Money (BigDecimal), AccountId, Currency
│   └── exception/             # InsufficientFundsException, DailyLimitExceededException
├── application/               # Capa de Orquestación y Casos de Uso
│   ├── port/in/               # ProcessPaymentUseCase, ProcessPaymentCommand
│   ├── port/out/              # LoadAccountPort, SavePaymentPort, AuditEventPublisherPort
│   └── usecase/               # ProcessPaymentService
└── adapter/                   # Capa de Infraestructura y Transporte
    ├── in/web/                # REST Controllers (PaymentController), DTOs, ExceptionHandler
    └── out/                   # Implementaciones de persistencia (JPA), Cuentas y Auditoría
```

### Reglas de Oro del Código
1. **Dominio Agnóstico**: Cero dependencias de Spring, JPA o HTTP en el paquete `domain`.
2. **Precisión Financiera Inviolable**: Prohibido el uso de `double` o `float` para montos. Se exige el Value Object inmutable `Money`.
3. **Encapsulamiento de Negocio**: `PaymentOrder` no tiene setters anémicos. Todo cambio de estado (`authorize()`, `settle()`, `reject()`) valida las invariantes de negocio.
4. **Fronteras Limpias**: Los controladores jamás exponen entidades `@Entity` de base de datos; utilizan DTOs mapeados en el borde.

---

## 2. Configuración Multi-Motor: Kodus y GitLab Duo

Las mismas 4 reglas arquitectónicas y de negocio están configuradas tanto para Kodus como para GitLab Duo Code Review:

| Regla | Archivo Kodus | Archivo GitLab Duo | Severidad | Propósito |
| :--- | :--- | :--- | :--- | :--- |
| **Aislamiento de Dominio** | `.kody/rules/domain-framework-isolation.md` | `.gitlab/duo/mr-review-instructions.yaml` | `CRITICAL` | Detecta si alguien importa `@Entity` o Spring en `domain`. |
| **Precisión Financiera** | `.kody/rules/financial-precision-money.md` | `.gitlab/duo/mr-review-instructions.yaml` | `HIGH` | Bloquea `float`/`double` para dinero, exigiendo `Money`. |
| **Anti-Deuda: No Entities en Controllers** | `.kody/rules/no-orm-entities-in-controllers.md` | `.gitlab/duo/mr-review-instructions.yaml` | `HIGH` | Evita exponer tablas JPA en endpoints REST. |
| **Invariantes de Negocio** | `.kody/rules/business-invariant-limits.md` | `.gitlab/duo/mr-review-instructions.yaml` | `HIGH` | Exige que validaciones y estados vivan en `PaymentOrder`. |

---

## 3. Guías de Demostración y Prueba

* **Demostración con Kodus**: Consulta [DEMO_WALKTHROUGH.md](file:///C:/Users/ktovar/kodus-java-backend-demo/DEMO_WALKTHROUGH.md) para ver los escenarios de revisión local, aprendizaje (Memories) y reducción de deuda técnica.
* **Prueba con GitLab Duo**: Consulta [GITLAB_DUO_TESTING.md](file:///C:/Users/ktovar/kodus-java-backend-demo/GITLAB_DUO_TESTING.md) para disparar revisiones en Merge Requests con `/assign_reviewer @GitLabDuo`.

---

## 4. Ejecución de Pruebas Unitarias

```bash
mvn clean test
```
*Total de pruebas*: 12 ejecutadas, 0 errores, 0 fallos (cobertura sobre lógica de redondeo, invariantes y puertos).
