package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects; // Adicionado para equals e hashCode

/**
 * Representa um veículo no sistema.
 * Corresponde à tabela 'vehicles' no banco de dados.
 */
public class Vehicle {
    private int id; // Renomeado de vehicleId para id para consistência
    private String plate;
    private String model;
    private int year;
    private int companyId; // Mantido para alinhar com o schema corrigido
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Adicionado para consistência com o schema e DAOs

    public Vehicle() {
    }

    // Construtor completo
    public Vehicle(int id, String plate, String model, int year, int companyId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.plate = plate;
        this.model = model;
        this.year = year;
        this.companyId = companyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Vehicle(String plate, String model, int year, int companyId) {
        this(0, plate, model, year, companyId, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Vehicle(int id, String plate, String model, int year, int companyId) {
        this(id, plate, model, year, companyId, null, null);
    }

    // Getters e Setters
    public int getId() { // Getter renomeado
        return id;
    }

    public void setId(int id) { // Setter renomeado
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public LocalDateTime getUpdatedAt() { // Getter adicionado
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { // Setter adicionado
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", plate='" + plate + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", companyId=" + companyId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && year == vehicle.year && companyId == vehicle.companyId && Objects.equals(plate, vehicle.plate) && Objects.equals(model, vehicle.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plate, model, year, companyId);
    }
}
