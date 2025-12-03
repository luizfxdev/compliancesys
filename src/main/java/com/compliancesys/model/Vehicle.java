package com.compliancesys.model;

import java.math.BigDecimal;    // Importa para usar BigDecimal para load_capacity.
import java.time.LocalDateTime; // Importa para usar LocalDateTime para a data de criação.

/**
 * Representa um veículo no sistema.
 * Corresponde à tabela 'Vehicle' no banco de dados.
 */
public class Vehicle {
    private int vehicleId; // ID único do veículo.
    private String licensePlate; // Placa do veículo, única e não nula.
    private String model; // Modelo do veículo.
    private int year; // Ano de fabricação do veículo.
    private BigDecimal loadCapacity; // Capacidade de carga do veículo.
    private int companyId; // ID da empresa à qual o veículo pertence.
    private LocalDateTime createdAt; // Timestamp de criação do registro.

    /**
     * Construtor padrão.
     */
    public Vehicle() {
    }

    /**
     * Construtor com todos os campos.
     * @param vehicleId ID do veículo.
     * @param licensePlate Placa do veículo.
     * @param model Modelo do veículo.
     * @param year Ano de fabricação.
     * @param loadCapacity Capacidade de carga.
     * @param companyId ID da empresa.
     * @param createdAt Data e hora de criação.
     */
    public Vehicle(int vehicleId, String licensePlate, String model, int year, BigDecimal loadCapacity, int companyId, LocalDateTime createdAt) {
        this.vehicleId = vehicleId;
        this.licensePlate = licensePlate;
        this.model = model;
        this.year = year;
        this.loadCapacity = loadCapacity;
        this.companyId = companyId;
        this.createdAt = createdAt;
    }

    // Getters e Setters para todos os campos.

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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

    public BigDecimal getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(BigDecimal loadCapacity) {
        this.loadCapacity = loadCapacity;
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
     * Retorna uma representação em String do objeto Vehicle.
     * @return String formatada.
     */
    @Override
    public String toString() {
        return "Vehicle{" +
               "vehicleId=" + vehicleId +
               ", licensePlate='" + licensePlate + '\'' +
               ", model='" + model + '\'' +
               ", year=" + year +
               ", loadCapacity=" + loadCapacity +
               ", companyId=" + companyId +
               ", createdAt=" + createdAt +
               '}';
    }
}
