package com.compliancesys.model;

import java.time.LocalDateTime; // Importa para usar LocalDateTime.

/**
 * Representa uma empresa no sistema.
 * Corresponde à tabela 'Company' no banco de dados.
 */
public class Company {
    private int companyId; // ID único da empresa.
    private String cnpj; // CNPJ da empresa, único e não nulo.
    private String legalName; // Razão social da empresa, não nula.
    private String tradingName; // Nome fantasia da empresa.
    private LocalDateTime createdAt; // Timestamp de criação do registro.

    /**
     * Construtor padrão.
     */
    public Company() {
    }

    /**
     * Construtor com todos os campos.
     * @param companyId ID da empresa.
     * @param cnpj CNPJ da empresa.
     * @param legalName Razão social.
     * @param tradingName Nome fantasia.
     * @param createdAt Data e hora de criação.
     */
    public Company(int companyId, String cnpj, String legalName, String tradingName, LocalDateTime createdAt) {
        this.companyId = companyId;
        this.cnpj = cnpj;
        this.legalName = legalName;
        this.tradingName = tradingName;
        this.createdAt = createdAt;
    }

    // Getters e Setters para todos os campos.

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getLegalName() {
        return legalName;
    }

    public void voidsetLegalName(String legalName) {
        this.legalName = legalName;
    }

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

    /**
     * Retorna uma representação em String do objeto Company.
     * @return String formatada.
     */
    @Override
    public String toString() {
        return "Company{" +
               "companyId=" + companyId +
               ", cnpj='" + cnpj + '\'' +
               ", legalName='" + legalName + '\'' +
               ", tradingName='" + tradingName + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
