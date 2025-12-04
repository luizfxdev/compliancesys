package com.compliancesys.model;

import com.compliancesys.model.enums.ComplianceStatus; // Importa o enum ComplianceStatus
import java.time.Duration; // Importa Duration
import java.time.LocalDate;
import java.time.LocalDateTime; // Importa LocalDateTime
import java.util.Objects;

/**
 * Representa uma jornada de trabalho de um motorista em um dia específico.
 * Corresponde à tabela 'journeys' no banco de dados.
 */
public class Journey {
    private int id;
    private int driverId;
    private LocalDate journeyDate;
    private Duration totalDrivingTime; // Alterado para Duration
    private Duration totalRestTime;    // Alterado para Duration
    private ComplianceStatus complianceStatus; // Alterado para o enum ComplianceStatus
    private boolean dailyLimitExceeded; // Adicionado para alinhar com o schema
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Adicionado para consistência com o schema e DAOs

    public Journey() {
    }

    // Construtor completo
    public Journey(int id, int driverId, LocalDate journeyDate, Duration totalDrivingTime, Duration totalRestTime, ComplianceStatus complianceStatus, boolean dailyLimitExceeded, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.journeyDate = journeyDate;
        this.totalDrivingTime = totalDrivingTime;
        this.totalRestTime = totalRestTime;
        this.complianceStatus = complianceStatus;
        this.dailyLimitExceeded = dailyLimitExceeded;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Journey(int driverId, LocalDate journeyDate, Duration totalDrivingTime, Duration totalRestTime, ComplianceStatus complianceStatus, boolean dailyLimitExceeded) {
        this(0, driverId, journeyDate, totalDrivingTime, totalRestTime, complianceStatus, dailyLimitExceeded, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Journey(int id, int driverId, LocalDate journeyDate, Duration totalDrivingTime, Duration totalRestTime, ComplianceStatus complianceStatus, boolean dailyLimitExceeded) {
        this(id, driverId, journeyDate, totalDrivingTime, totalRestTime, complianceStatus, dailyLimitExceeded, null, null);
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

    public Duration getTotalDrivingTime() { // Getter alterado para Duration
        return totalDrivingTime;
    }

    public void setTotalDrivingTime(Duration totalDrivingTime) { // Setter alterado para Duration
        this.totalDrivingTime = totalDrivingTime;
    }

    public Duration getTotalRestTime() { // Getter alterado para Duration
        return totalRestTime;
    }

    public void setTotalRestTime(Duration totalRestTime) { // Setter alterado para Duration
        this.totalRestTime = totalRestTime;
    }

    public ComplianceStatus getComplianceStatus() { // Getter alterado para ComplianceStatus
        return complianceStatus;
    }

    public void setComplianceStatus(ComplianceStatus complianceStatus) { // Setter alterado para ComplianceStatus
        this.complianceStatus = complianceStatus;
    }

    public boolean isDailyLimitExceeded() { // Getter adicionado
        return dailyLimitExceeded;
    }

    public void setDailyLimitExceeded(boolean dailyLimitExceeded) { // Setter adicionado
        this.dailyLimitExceeded = dailyLimitExceeded;
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
        return "Journey{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", journeyDate=" + journeyDate +
                ", totalDrivingTime=" + totalDrivingTime +
                ", totalRestTime=" + totalRestTime +
                ", complianceStatus=" + complianceStatus +
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
        return id == journey.id && driverId == journey.driverId && dailyLimitExceeded == journey.dailyLimitExceeded && Objects.equals(journeyDate, journey.journeyDate) && Objects.equals(totalDrivingTime, journey.totalDrivingTime) && Objects.equals(totalRestTime, journey.totalRestTime) && complianceStatus == journey.complianceStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, journeyDate, totalDrivingTime, totalRestTime, complianceStatus, dailyLimitExceeded);
    }
}
