package com.compliancesys.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um motorista no sistema.
 * Corresponde à tabela 'drivers' no banco de dados.
 */
public class Driver {
    private int id;
    private String name;
    private String cpf;
    private LocalDate birthDate; // Alterado para LocalDate
    private String licenseNumber;
    // private int companyId; // Removido para alinhar com o schema atual
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Driver() {
    }

    // Construtor completo
    public Driver(int id, String name, String cpf, LocalDate birthDate, String licenseNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
        this.licenseNumber = licenseNumber;
        // this.companyId = companyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Driver(String name, String cpf, LocalDate birthDate, String licenseNumber) {
        this(0, name, cpf, birthDate, licenseNumber, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Driver(int id, String name, String cpf, LocalDate birthDate, String licenseNumber) {
        this(id, name, cpf, birthDate, licenseNumber, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /*
    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
    */

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
                ", name='" + name + '\'' +
                ", cpf='" + cpf + '\'' +
                ", birthDate=" + birthDate +
                ", licenseNumber='" + licenseNumber + '\'' +
                // ", companyId=" + companyId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && Objects.equals(name, driver.name) && Objects.equals(cpf, driver.cpf) && Objects.equals(birthDate, driver.birthDate) && Objects.equals(licenseNumber, driver.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, birthDate, licenseNumber);
    }
}
