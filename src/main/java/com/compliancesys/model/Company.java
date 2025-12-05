package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma empresa no sistema.
 * Corresponde à tabela 'companies' no banco de dados.
 */
public class Company {
    private int id;
    private String name;
    private String cnpj; // ADICIONADO
    private String email; // ADICIONADO
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Company() {
    }

    // Construtor completo
    public Company(int id, String name, String cnpj, String email, String phone, String address, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Company(String name, String cnpj, String email, String phone, String address) {
        this(0, name, cnpj, email, phone, address, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Company(int id, String name, String cnpj, String email, String phone, String address) {
        this(id, name, cnpj, email, phone, address, null, null);
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

    public String getCnpj() { // ADICIONADO
        return cnpj;
    }

    public void setCnpj(String cnpj) { // ADICIONADO
        this.cnpj = cnpj;
    }

    public String getEmail() { // ADICIONADO
        return email;
    }

    public void setEmail(String email) { // ADICIONADO
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id == company.id &&
                Objects.equals(name, company.name) &&
                Objects.equals(cnpj, company.cnpj) &&
                Objects.equals(email, company.email) &&
                Objects.equals(phone, company.phone) &&
                Objects.equals(address, company.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cnpj, email, phone, address);
    }
}
