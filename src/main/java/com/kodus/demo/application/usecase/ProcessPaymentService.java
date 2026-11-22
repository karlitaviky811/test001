package com.kodus.demo.application.usecase;

import com.kodus.demo.application.port.in.ProcessPaymentCommand;
import com.kodus.demo.application.port.in.ProcessPaymentUseCase;
import com.kodus.demo.application.port.out.AuditEventPublisherPort;
import com.kodus.demo.application.port.out.LoadAccountPort;
import com.kodus.demo.application.port.out.SavePaymentPort;
import com.kodus.demo.domain.exception.BusinessRuleViolationException;
import com.kodus.demo.domain.model.PaymentId;
import com.kodus.demo.domain.model.PaymentOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Caso de uso: Procesa la orquestación del pago.
 * 
 * Cumple con inversión de dependencias: solo conoce puertos de entrada/salida y entidades de dominio.
 */
@Service
public class ProcessPaymentService implements ProcessPaymentUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SavePaymentPort savePaymentPort;
    private final AuditEventPublisherPort auditEventPublisherPort;

    public ProcessPaymentService(
            LoadAccountPort loadAccountPort,
            SavePaymentPort savePaymentPort,
            AuditEventPublisherPort auditEventPublisherPort) {
        this.loadAccountPort = Objects.requireNonNull(loadAccountPort);
        this.savePaymentPort = Objects.requireNonNull(savePaymentPort);
        this.auditEventPublisherPort = Objects.requireNonNull(auditEventPublisherPort);
    }

    @Override
    @Transactional
    public PaymentOrder process(ProcessPaymentCommand command) {
        // 1. Verificación de Idempotencia
        Optional<PaymentOrder> existing = savePaymentPort.findByIdempotencyKey(command.idempotencyKey());
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2. Cargar estado de la cuenta origen
        LoadAccountPort.AccountSnapshot sourceAccount = loadAccountPort.loadAccount(command.sourceAccountId())
                .orElseThrow(() -> new BusinessRuleViolationException("Source account not found: " + command.sourceAccountId().value()));

        // 3. Crear entidad de dominio
        PaymentOrder payment = new PaymentOrder(
                PaymentId.generate(),
                command.sourceAccountId(),
                command.targetAccountId(),
                command.amount()
        );

        // 4. Ejecutar reglas de negocio en la entidad
        try {
            payment.authorize(
                    sourceAccount.currentBalance(),
                    sourceAccount.dailySpent(),
                    sourceAccount.dailyLimit()
            );
            payment.settle();
        } catch (Exception ex) {
            // Guardar registro del pago rechazado para trazabilidad y auditoría
            savePaymentPort.save(payment, command.idempotencyKey());
            auditEventPublisherPort.publishPaymentProcessed(payment, command.idempotencyKey());
            throw ex;
        }

        // 5. Persistir y emitir evento
        PaymentOrder saved = savePaymentPort.save(payment, command.idempotencyKey());
        auditEventPublisherPort.publishPaymentProcessed(saved, command.idempotencyKey());

        return saved;
    }
}
