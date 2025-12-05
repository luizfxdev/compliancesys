package com.compliancesys.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.ComplianceStatus;

/**
 * Representa uma jornada de trabalho de um motorista em um dia específico.
 * Corresponde à tabela 'journeys' no banco de dados.
 */
public class Journey {
    private int id;
    private int driverId;
    private int vehicleId;
    private LocalDate journeyDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startLocation;
    private String endLocation;
    private Duration totalDrivingTime;
    private Duration totalRestTime;
    private Duration totalBreakTime;
    private ComplianceStatus status;
    private boolean dailyLimitExceeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Journey() {
    }

    public Journey(int id, int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime, String startLocation, String endLocation, Duration totalDrivingTime, Duration totalRestTime, Duration totalBreakTime, ComplianceStatus status, boolean dailyLimitExceeded, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.journeyDate = journeyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.totalDrivingTime = totalDrivingTime;
        this.totalRestTime = totalRestTime;
        this.totalBreakTime = totalBreakTime;
        this.status = status;
        this.dailyLimitExceeded = dailyLimitExceeded;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Journey(int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime, String startLocation, String endLocation, Duration totalDrivingTime, Duration totalRestTime, Duration totalBreakTime, ComplianceStatus status, boolean dailyLimitExceeded) {
        this(0, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation, totalDrivingTime, totalRestTime, totalBreakTime, status, dailyLimitExceeded, null, null);
    }

    public Journey(int id, int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime, String startLocation, String endLocation, Duration totalDrivingTime, Duration totalRestTime, Duration totalBreakTime, ComplianceStatus status, boolean dailyLimitExceeded) {
        this(id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation, totalDrivingTime, totalRestTime, totalBreakTime, status, dailyLimitExceeded, null, null);
    }

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

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public Duration getTotalDrivingTime() {
        return totalDrivingTime;
    }

    public void setTotalDrivingTime(Duration totalDrivingTime) {
        this.totalDrivingTime = totalDrivingTime;
    }

    public Duration getTotalRestTime() {
        return totalRestTime;
    }

    public void setTotalRestTime(Duration totalRestTime) {
        this.totalRestTime = totalRestTime;
    }

    public Duration getTotalBreakTime() {
        return totalBreakTime;
    }

    public void setTotalBreakTime(Duration totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }

    public ComplianceStatus getStatus() {
        return status;
    }

    public void setStatus(ComplianceStatus status) {
        this.status = status;
    }

    // Métodos alias para compatibilidade com código existente
    public ComplianceStatus getComplianceStatus() {
        return status;
    }

    public void setComplianceStatus(ComplianceStatus status) {
        this.status = status;
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
                ", journeyDate=" + journeyDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", totalDrivingTime=" + totalDrivingTime +
                ", totalRestTime=" + totalRestTime +
                ", totalBreakTime=" + totalBreakTime +
                ", status=" + status +
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
                dailyLimitExceeded == journey.dailyLimitExceeded &&
                Objects.equals(journeyDate, journey.journeyDate) &&
                Objects.equals(startTime, journey.startTime) &&
                Objects.equals(endTime, journey.endTime) &&
                Objects.equals(startLocation, journey.startLocation) &&
                Objects.equals(endLocation, journey.endLocation) &&
                Objects.equals(totalDrivingTime, journey.totalDrivingTime) &&
                Objects.equals(totalRestTime, journey.totalRestTime) &&
                Objects.equals(totalBreakTime, journey.totalBreakTime) &&
                status == journey.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation, totalDrivingTime, totalRestTime, totalBreakTime, status, dailyLimitExceeded);
    }
}