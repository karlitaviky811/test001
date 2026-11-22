# Directrices de Repositorio para GitHub Copilot y Kodus

Este proyecto implementa un Core de Pagos bajo **Arquitectura Hexagonal (Ports & Adapters)** y **Domain-Driven Design (DDD)**.

## Reglas Clave
1. **Dominio Puro (`src/main/java/**/domain/**`)**: Prohibido importar dependencias de Spring, JPA/Hibernate o transporte web.
2. **Precisión Financiera (`Money`)**: Prohibido el uso de `float`, `double` para dinero. Siempre utilizar `com.kodus.demo.domain.model.Money` o `BigDecimal` con `RoundingMode.HALF_EVEN`.
3. **Controladores REST (`adapter.in.web`)**: Nunca exponer o recibir `@Entity`. Usar siempre DTOs (`PaymentRequestDto`, `PaymentResponseDto`).
4. **Encapsulamiento de Negocio**: Las transiciones de estado de pago se ejecutan en los métodos del agregado `PaymentOrder` (`authorize()`, `settle()`, `reject()`), jamás con setters anémicos.
