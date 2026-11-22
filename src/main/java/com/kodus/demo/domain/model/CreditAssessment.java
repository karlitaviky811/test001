package com.kodus.demo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ❌ VIOLACIÓN CRÍTICA (Caso idéntico al probado en GitLab):
 * Modelo dentro de domain anotado con @Entity, @Table y @Component de Spring,
 * destruyendo por completo el aislamiento arquitectónico del Dominio Hexagonal.
 */
@Entity
@Table(name = "credit_assessments")
@Component
public class CreditAssessment {

    @Id
    private UUID id;

    @Column(name = "score")
    private int riskScore;

    @Column(name = "approved")
    private boolean isApproved;

    public CreditAssessment() {}

    public CreditAssessment(UUID id, int riskScore, boolean isApproved) {
        this.id = id;
        this.riskScore = riskScore;
        this.isApproved = isApproved;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
}
