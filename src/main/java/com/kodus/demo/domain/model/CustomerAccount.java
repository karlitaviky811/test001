package com.kodus.demo.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * ❌ VIOLACIÓN CRÍTICA DE ARQUITECTURA HEXAGONAL:
 * Esta clase reside en el paquete `domain`, pero importa e implementa anotaciones
 * de persistencia de frameworks (@Entity, @Table, @Id de jakarta.persistence).
 * 
 * Regla violada: "Aislamiento de Dominio - Arquitectura Hexagonal".
 */
@Entity
@Table(name = "customer_accounts")
public class CustomerAccount {

    @Id
    private UUID id;
    private String holderName;
    private String taxIdentificationNumber;

    public CustomerAccount() {}

    public CustomerAccount(UUID id, String holderName, String taxIdentificationNumber) {
        this.id = id;
        this.holderName = holderName;
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getTaxIdentificationNumber() { return taxIdentificationNumber; }
    public void setTaxIdentificationNumber(String taxIdentificationNumber) { this.taxIdentificationNumber = taxIdentificationNumber; }
}
