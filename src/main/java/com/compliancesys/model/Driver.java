// src/main/java/com/compliancesys/model/Driver.java
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
    private int companyId; // Mantido, assumindo que é usado na lógica de negócio ou em outras tabelas.
    private String name;
    private String cpf;
    private String licenseNumber;
    private String licenseCategory;
    private LocalDate licenseExpiration; // Nome do campo já está correto
    private LocalDate birthDate;
    private String phone;
    private String email; // Campo já está correto
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Driver() {
    }

    // Construtor completo com todos os campos (12 parâmetros)
    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.cpf = cpf;
        this.licenseNumber = licenseNumber;
        this.licenseCategory = licenseCategory;
        this.licenseExpiration = licenseExpiration;
        this.birthDate = birthDate;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt) (10 parâmetros)
    public Driver(int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email) {
        this(0, companyId, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, phone, email, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt) (10 parâmetros)
    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email) {
        this(id, companyId, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, phone, email, null, null);
    }

    // Construtor para compatibilidade com testes (sem companyId) (11 parâmetros)
    // Este construtor é um pouco problemático se companyId é um campo obrigatório na lógica.
    // Se for apenas para testes, tudo bem, mas é bom ter em mente.
    public Driver(int id, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, 0, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, phone, email, createdAt, updatedAt);
    }

    // NOVO CONSTRUTOR ADICIONADO PARA FACILITAR TESTES E ALINHAR COM O USO COMUM
    // Este construtor cobre os campos essenciais para a maioria das instâncias de teste
    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate) {
        this(id, companyId, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, null, null, null, null); // Phone, email, createdAt, updatedAt como null
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

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    // Getter e Setter para licenseExpiration (já estavam corretos)
    public LocalDate getLicenseExpiration() {
        return licenseExpiration;
    }

    public void setLicenseExpiration(LocalDate licenseExpiration) {
        this.licenseExpiration = licenseExpiration;
    }

    // Getter e Setter para birthDate (já estavam corretos)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter e Setter para email (já estavam corretos)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        Driver driver = (Driver) o;
        return id == driver.id &&
               companyId == driver.companyId &&
               Objects.equals(name, driver.name) &&
               Objects.equals(cpf, driver.cpf) &&
               Objects.equals(licenseNumber, driver.licenseNumber) &&
               Objects.equals(licenseCategory, driver.licenseCategory) &&
               Objects.equals(licenseExpiration, driver.licenseExpiration) &&
               Objects.equals(birthDate, driver.birthDate) &&
               Objects.equals(phone, driver.phone) &&
               Objects.equals(email, driver.email) &&
               Objects.equals(createdAt, driver.createdAt) &&
               Objects.equals(updatedAt, driver.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyId, name, cpf, licenseNumber, licenseCategory,
                            licenseExpiration, birthDate, phone, email, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Driver{" +
               "id=" + id +
               ", companyId=" + companyId +
               ", name='" + name + '\'' +
               ", cpf='" + cpf + '\'' +
               ", licenseNumber='" + licenseNumber + '\'' +
               ", licenseCategory='" + licenseCategory + '\'' +
               ", licenseExpiration=" + licenseExpiration +
               ", birthDate=" + birthDate +
               ", phone='" + phone + '\'' +
               ", email='" + email + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
