package com.kodus.demo.application.usecase;

import com.kodus.demo.application.port.in.ProcessPaymentCommand;
import com.kodus.demo.application.port.out.AuditEventPublisherPort;
import com.kodus.demo.application.port.out.LoadAccountPort;
import com.kodus.demo.application.port.out.SavePaymentPort;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentOrder;
import com.kodus.demo.domain.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentServiceTest {

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private SavePaymentPort savePaymentPort;

    @Mock
    private AuditEventPublisherPort auditEventPublisherPort;

    private ProcessPaymentService processPaymentService;

    @BeforeEach
    void setUp() {
        processPaymentService = new ProcessPaymentService(loadAccountPort, savePaymentPort, auditEventPublisherPort);
    }

    @Test
    @DisplayName("Debe retornar orden existente si la clave de idempotencia ya fue procesada")
    void shouldReturnExistingPaymentWhenIdempotencyKeyExists() {
        String idemKey = "IDEM-12345";
        PaymentOrder existing = new PaymentOrder(
                com.kodus.demo.domain.model.PaymentId.generate(),
                new AccountId("ACC-1"),
                new AccountId("ACC-2"),
                Money.of(new BigDecimal("100.00"), Currency.USD)
        );
        when(savePaymentPort.findByIdempotencyKey(idemKey)).thenReturn(Optional.of(existing));

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                new AccountId("ACC-1"),
                new AccountId("ACC-2"),
                Money.of(new BigDecimal("100.00"), Currency.USD),
                idemKey
        );

        PaymentOrder result = processPaymentService.process(command);

        assertThat(result).isSameAs(existing);
        verify(loadAccountPort, never()).loadAccount(any());
    }

    @Test
    @DisplayName("Debe procesar y liquidar un pago cuando las reglas de negocio se cumplen")
    void shouldProcessPaymentSuccessfully() {
        String idemKey = "IDEM-99999";
        AccountId source = new AccountId("ACC-1");
        AccountId target = new AccountId("ACC-2");

        when(savePaymentPort.findByIdempotencyKey(idemKey)).thenReturn(Optional.empty());
        when(loadAccountPort.loadAccount(source)).thenReturn(Optional.of(
                new LoadAccountPort.AccountSnapshot(
                        source,
                        Money.of(new BigDecimal("5000.00"), Currency.USD),
                        Money.of(new BigDecimal("100.00"), Currency.USD),
                        Money.of(new BigDecimal("2000.00"), Currency.USD)
                )
        ));
        when(savePaymentPort.save(any(PaymentOrder.class), eq(idemKey))).thenAnswer(inv -> inv.getArgument(0));

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                source,
                target,
                Money.of(new BigDecimal("250.00"), Currency.USD),
                idemKey
        );

        PaymentOrder processed = processPaymentService.process(command);

        assertThat(processed.getStatus()).isEqualTo(PaymentStatus.SETTLED);
        verify(savePaymentPort, times(1)).save(any(), eq(idemKey));
        verify(auditEventPublisherPort, times(1)).publishPaymentProcessed(any(), eq(idemKey));
    }
}
