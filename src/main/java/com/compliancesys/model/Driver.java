package com.compliancesys.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Driver {
    private int id;
    private int companyId;
    private String name;
    private String cpf;
    private String licenseNumber;
    private String licenseCategory;
    private LocalDate licenseExpiration;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Driver() {
    }

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

    public Driver(int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email) {
        this(0, companyId, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, phone, email, null, null);
    }

    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email) {
        this(id, companyId, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, phone, email, null, null);
    }

    public Driver(int id, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate, String phone, String email,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, 0, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, phone, email, createdAt, updatedAt);
    }

    public Driver(int id, int companyId, String name, String cpf, String licenseNumber, String licenseCategory,
                  LocalDate licenseExpiration, LocalDate birthDate) {
        this(id, companyId, name, cpf, licenseNumber, licenseCategory, licenseExpiration,
             birthDate, null, null, null, null);
    }

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

    public LocalDate getLicenseExpiration() {
        return licenseExpiration;
    }

    public void setLicenseExpiration(LocalDate licenseExpiration) {
        this.licenseExpiration = licenseExpiration;
    }

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
