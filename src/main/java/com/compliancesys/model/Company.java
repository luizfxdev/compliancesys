// src/main/java/com/compliancesys/model/Company.java
package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma empresa no sistema.
 * Corresponde à tabela 'companies' no banco de dados.
 */
public class Company {
    private int id;
    private String cnpj;
    private String legalName; // ADICIONADO: Campo legalName
    private String tradingName; // ADICIONADO: Campo tradingName
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Company() {
    }

    // Construtor completo
    public Company(int id, String cnpj, String legalName, String tradingName,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.cnpj = cnpj;
        this.legalName = legalName;
        this.tradingName = tradingName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Company(String cnpj, String legalName, String tradingName) {
        this(0, cnpj, legalName, tradingName, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Company(int id, String cnpj, String legalName, String tradingName) {
        this(id, cnpj, legalName, tradingName, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    // ADICIONADO: Getter e Setter para legalName
    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    // ADICIONADO: Getter e Setter para tradingName
    public String getTradingName() {
        return tradingName;
    }

    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id == company.id &&
               Objects.equals(cnpj, company.cnpj) &&
               Objects.equals(legalName, company.legalName) &&
               Objects.equals(tradingName, company.tradingName) &&
               Objects.equals(createdAt, company.createdAt) &&
               Objects.equals(updatedAt, company.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cnpj, legalName, tradingName, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Company{" +
               "id=" + id +
               ", cnpj='" + cnpj + '\'' +
               ", legalName='" + legalName + '\'' +
               ", tradingName='" + tradingName + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
