# Kodus Java Backend Demo: Core de Pagos con Arquitectura Hexagonal

Backend de demostración en **Java 21** y **Spring Boot 3.3.5** diseñado específicamente para ilustrar cómo **Kodus (Self-Hosted AI Code Review)** detecta violaciones de reglas de negocio, aprende de las decisiones del equipo y guía la eliminación progresiva de deuda técnica.

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

## 2. Integración con Kodus (Policy as Code)

Kodus monitorea y evalúa cada Pull Request mediante reglas declarativas en `.kody/rules/` y directrices de repositorio en `CLAUDE.md`:

| Regla | Ubicación | Severidad | Propósito |
| :--- | :--- | :--- | :--- |
| **Aislamiento de Dominio** | `.kody/rules/domain-framework-isolation.md` | `CRITICAL` | Detecta fugas de frameworks (`@Entity`, `@Service`) dentro de `domain`. |
| **Precisión Financiera** | `.kody/rules/financial-precision-money.md` | `HIGH` | Bloquea el uso de `float`/`double` en cálculos y variables de dinero. |
| **Anti-Deuda: No Entities en Controllers** | `.kody/rules/no-orm-entities-in-controllers.md` | `HIGH` | Evita exponer tablas JPA en endpoints REST para evitar acoplamiento. |
| **Validación de Invariantes de Negocio** | `.kody/rules/business-invariant-limits.md` | `HIGH` | Asegura que las validaciones de saldo y límite diario residan en la entidad. |

---

## 3. Comandos Útiles del CLI de Kodus

```bash
# 1. Ver las reglas activas configuradas
kodus rules view --repo-id global

# 2. Ejecutar revisión local sobre los cambios pendientes
kodus review

# 3. Validar cambios locales frente a reglas de negocio y criterios de aceptación
kodus pr business-validation
```

---

## 4. Guía Rápida de Ejecución de la Demo

Para ver en acción los escenarios de detección de deuda técnica y aprendizaje de Kodus, consulta la guía paso a paso en [DEMO_WALKTHROUGH.md](file:///C:/Users/ktovar/kodus-java-backend-demo/DEMO_WALKTHROUGH.md).
