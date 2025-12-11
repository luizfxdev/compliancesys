package com.compliancesys.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma jornada de trabalho de um motorista.
 * Corresponde à tabela 'journeys' no banco de dados.
 * Alinhado com o schema.sql fornecido.
 */
public class Journey {
    private int id;
    private int driverId;
    private LocalDate journeyDate;
    private int totalDrivingTimeMinutes; // Alinhado com schema.sql
    private int totalRestTimeMinutes;    // Alinhado com schema.sql
    private String complianceStatus;     // Alinhado com schema.sql (VARCHAR)
    private boolean dailyLimitExceeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Journey() {
    }

    // Construtor completo
    public Journey(int id, int driverId, LocalDate journeyDate, int totalDrivingTimeMinutes,
                   int totalRestTimeMinutes, String complianceStatus, boolean dailyLimitExceeded,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.journeyDate = journeyDate;
        this.totalDrivingTimeMinutes = totalDrivingTimeMinutes;
        this.totalRestTimeMinutes = totalRestTimeMinutes;
        this.complianceStatus = complianceStatus;
        this.dailyLimitExceeded = dailyLimitExceeded;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Journey(int driverId, LocalDate journeyDate, int totalDrivingTimeMinutes,
                   int totalRestTimeMinutes, String complianceStatus, boolean dailyLimitExceeded) {
        this(0, driverId, journeyDate, totalDrivingTimeMinutes, totalRestTimeMinutes,
             complianceStatus, dailyLimitExceeded, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Journey(int id, int driverId, LocalDate journeyDate, int totalDrivingTimeMinutes,
                   int totalRestTimeMinutes, String complianceStatus, boolean dailyLimitExceeded) {
        this(id, driverId, journeyDate, totalDrivingTimeMinutes, totalRestTimeMinutes,
             complianceStatus, dailyLimitExceeded, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
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
                ", journeyDate=" + journeyDate +
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
                totalDrivingTimeMinutes == journey.totalDrivingTimeMinutes &&
                totalRestTimeMinutes == journey.totalRestTimeMinutes &&
                dailyLimitExceeded == journey.dailyLimitExceeded &&
                Objects.equals(journeyDate, journey.journeyDate) &&
                Objects.equals(complianceStatus, journey.complianceStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, journeyDate, totalDrivingTimeMinutes,
                            totalRestTimeMinutes, complianceStatus, dailyLimitExceeded);
    }
}
