// src/main/java/com/compliancesys/model/Journey.java
package com.compliancesys.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma jornada de trabalho de um motorista.
 * Corresponde à tabela 'journeys' no banco de dados.
 * Alinhado com o schema.sql fornecido e estendido para suportar chamadas existentes no código.
 */
public class Journey {
    private int id;
    private int driverId;
    private int vehicleId; // Adicionado para satisfazer chamadas como journey.getVehicleId()
    private int companyId; // Adicionado para satisfazer chamadas como journey.getCompanyId()
    private LocalDate journeyDate;
    private String startLocation; // Adicionado para satisfazer chamadas como journey.getStartLocation()
    private int totalDrivingTimeMinutes; // Alinhado com schema.sql
    private int totalRestTimeMinutes;    // Alinhado com schema.sql
    private String complianceStatus;     // Alinhado com schema.sql (VARCHAR)
    private boolean dailyLimitExceeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Journey() {
    }

    // Construtor completo
    public Journey(int id, int driverId, int vehicleId, int companyId, LocalDate journeyDate, String startLocation,
                   int totalDrivingTimeMinutes, int totalRestTimeMinutes, String complianceStatus,
                   boolean dailyLimitExceeded, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.companyId = companyId;
        this.journeyDate = journeyDate;
        this.startLocation = startLocation;
        this.totalDrivingTimeMinutes = totalDrivingTimeMinutes;
        this.totalRestTimeMinutes = totalRestTimeMinutes;
        this.complianceStatus = complianceStatus;
        this.dailyLimitExceeded = dailyLimitExceeded;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Journey(int driverId, int vehicleId, int companyId, LocalDate journeyDate, String startLocation,
                   int totalDrivingTimeMinutes, int totalRestTimeMinutes, String complianceStatus,
                   boolean dailyLimitExceeded) {
        this(0, driverId, vehicleId, companyId, journeyDate, startLocation,
             totalDrivingTimeMinutes, totalRestTimeMinutes, complianceStatus,
             dailyLimitExceeded, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Adicionado para satisfazer chamadas que esperam getJourneyId()
    public int getJourneyId() {
        return id;
    }

    public void setJourneyId(int journeyId) {
        this.id = journeyId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    // getStartDate() pode ser um alias para getJourneyDate()
    public LocalDate getStartDate() {
        return journeyDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.journeyDate = startDate;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public int getTotalDrivingTimeMinutes() {
        return totalDrivingTimeMinutes;
    }

    public void setTotalDrivingTimeMinutes(int totalDrivingTimeMinutes) {
        this.totalDrivingTimeMinutes = totalDrivingTimeMinutes;
    }

    public int getTotalRestTimeMinutes() {
        return totalRestTimeMinutes;
    }

    public void setTotalRestTimeMinutes(int totalRestTimeMinutes) {
        this.totalRestTimeMinutes = totalRestTimeMinutes;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public boolean isDailyLimitExceeded() {
        return dailyLimitExceeded;
    }

    public void setDailyLimitExceeded(boolean dailyLimitExceeded) {
        this.dailyLimitExceeded = dailyLimitExceeded;
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
        return "Journey{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", vehicleId=" + vehicleId +
                ", companyId=" + companyId +
                ", journeyDate=" + journeyDate +
                ", startLocation='" + startLocation + '\'' +
                ", totalDrivingTimeMinutes=" + totalDrivingTimeMinutes +
                ", totalRestTimeMinutes=" + totalRestTimeMinutes +
                ", complianceStatus='" + complianceStatus + '\'' +
                ", dailyLimitExceeded=" + dailyLimitExceeded +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Journey journey = (Journey) o;
        return id == journey.id &&
                driverId == journey.driverId &&
                vehicleId == journey.vehicleId &&
                companyId == journey.companyId &&
                totalDrivingTimeMinutes == journey.totalDrivingTimeMinutes &&
                totalRestTimeMinutes == journey.totalRestTimeMinutes &&
                dailyLimitExceeded == journey.dailyLimitExceeded &&
                Objects.equals(journeyDate, journey.journeyDate) &&
                Objects.equals(startLocation, journey.startLocation) &&
                Objects.equals(complianceStatus, journey.complianceStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, vehicleId, companyId, journeyDate, startLocation,
                totalDrivingTimeMinutes, totalRestTimeMinutes, complianceStatus,
                dailyLimitExceeded);
    }
}
