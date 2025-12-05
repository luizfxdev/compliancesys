package com.compliancesys.model;

import java.time.LocalDate; // Importa LocalDate
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um motorista no sistema.
 * Corresponde à tabela 'drivers' no banco de dados.
 */
public class Driver {
    private int id;
    private int companyId;
    private String name;
    private String cpf;
    private String licenseNumber;
    private LocalDate birthDate; // ADICIONADO: Campo birthDate
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Driver() {
    }

    // Construtor completo
    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, LocalDate birthDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.cpf = cpf;
        this.licenseNumber = licenseNumber;
        this.birthDate = birthDate; // Inicializa birthDate
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Driver(int companyId, String name, String cpf, String licenseNumber, LocalDate birthDate) {
        this(0, companyId, name, cpf, licenseNumber, birthDate, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, LocalDate birthDate) {
        this(id, companyId, name, cpf, licenseNumber, birthDate, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getBirthDate() { // ADICIONADO: Getter para birthDate
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) { // ADICIONADO: Setter para birthDate
        this.birthDate = birthDate;
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
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", birthDate=" + birthDate + // Incluído no toString
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id &&
                companyId == driver.companyId &&
                Objects.equals(name, driver.name) &&
                Objects.equals(cpf, driver.cpf) &&
                Objects.equals(licenseNumber, driver.licenseNumber) &&
                Objects.equals(birthDate, driver.birthDate); // Incluído no equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyId, name, cpf, licenseNumber, birthDate); // Incluído no hashCode
    }
}
