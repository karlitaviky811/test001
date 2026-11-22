package com.kodus.demo.adapter.out.persistence;

import com.kodus.demo.application.port.out.SavePaymentPort;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentId;
import com.kodus.demo.domain.model.PaymentOrder;
import com.kodus.demo.domain.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentPersistenceAdapter implements SavePaymentPort {

    private final SpringDataPaymentRepository repository;

    public PaymentPersistenceAdapter(SpringDataPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentOrder save(PaymentOrder order, String idempotencyKey) {
        PaymentJpaEntity entity = toEntity(order, idempotencyKey);
        PaymentJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
    }

    @Override
    public Optional<PaymentOrder> findById(PaymentId id) {
        return repository.findById(id.value()).map(this::toDomain);
    }

    private PaymentJpaEntity toEntity(PaymentOrder domain, String idempotencyKey) {
        return new PaymentJpaEntity(
                domain.getId().value(),
                idempotencyKey,
                domain.getSourceAccountId().value(),
                domain.getTargetAccountId().value(),
                domain.getAmount().getAmount(),
                domain.getAmount().getCurrency().name(),
                domain.getStatus().name(),
                domain.getRejectionReason(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    private PaymentOrder toDomain(PaymentJpaEntity entity) {
        return PaymentOrder.rehydrate(
                new PaymentId(entity.getId()),
                new AccountId(entity.getSourceAccountId()),
                new AccountId(entity.getTargetAccountId()),
                Money.of(entity.getAmount(), Currency.valueOf(entity.getCurrency())),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
