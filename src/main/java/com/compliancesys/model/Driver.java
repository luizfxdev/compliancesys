package com.compliancesys.model;

import java.time.LocalDate;      // Importa para usar LocalDate para a data de contratação.
import java.time.LocalDateTime;  // Importa para usar LocalDateTime para a data de criação.

/**
 * Representa um motorista no sistema.
 * Corresponde à tabela 'Driver' no banco de dados.
 */
public class Driver {
    private int driverId; // ID único do motorista.
    private String name; // Nome completo do motorista.
    private String cpf; // CPF do motorista, único e não nulo.
    private String licenseCategory; // Categoria da CNH (ex: "C", "D", "E").
    private LocalDate hireDate; // Data de contratação do motorista.
    private int companyId; // ID da empresa à qual o motorista está vinculado.
    private LocalDateTime createdAt; // Timestamp de criação do registro.

    /**
     * Construtor padrão.
     */
    public Driver() {
    }

    /**
     * Construtor com todos os campos.
     * @param driverId ID do motorista.
     * @param name Nome do motorista.
     * @param cpf CPF do motorista.
     * @param licenseCategory Categoria da CNH.
     * @param hireDate Data de contratação.
     * @param companyId ID da empresa.
     * @param createdAt Data e hora de criação.
     */
    public Driver(int driverId, String name, String cpf, String licenseCategory, LocalDate hireDate, int companyId, LocalDateTime createdAt) {
        this.driverId = driverId;
        this.name = name;
        this.cpf = cpf;
        this.licenseCategory = licenseCategory;
        this.hireDate = hireDate;
        this.companyId = companyId;
        this.createdAt = createdAt;
    }

    // Getters e Setters para todos os campos.

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Retorna uma representação em String do objeto Driver.
     * @return String formatada.
     */
    @Override
    public String toString() {
        return "Driver{" +
               "driverId=" + driverId +
               ", name='" + name + '\'' +
               ", cpf='" + cpf + '\'' +
               ", licenseCategory='" + licenseCategory + '\'' +
               ", hireDate=" + hireDate +
               ", companyId=" + companyId +
               ", createdAt=" + createdAt +
               '}';
    }
}
