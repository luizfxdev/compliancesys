package com.compliancesys.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma jornada de trabalho de um motorista.
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
    private long totalDuration; // Duração total da jornada em minutos
    private long drivingDuration; // Duração de direção em minutos
    private long breakDuration; // Duração de pausas em minutos
    private long restDuration; // Duração de descanso em minutos
    private long mealDuration; // Duração de refeição em minutos
    private String status; // NOVO: Status da jornada (ex: "IN_PROGRESS", "COMPLETED", "VIOLATION")
    private boolean dailyLimitExceeded; // NOVO: Indica se algum limite diário foi excedido
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Journey() {
    }

    // Construtor completo
    public Journey(int id, int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime,
                   String startLocation, String endLocation, long totalDuration, long drivingDuration,
                   long breakDuration, long restDuration, long mealDuration, String status, boolean dailyLimitExceeded,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.journeyDate = journeyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.totalDuration = totalDuration;
        this.drivingDuration = drivingDuration;
        this.breakDuration = breakDuration;
        this.restDuration = restDuration;
        this.mealDuration = mealDuration;
        this.status = status;
        this.dailyLimitExceeded = dailyLimitExceeded;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para criação (sem ID, createdAt, updatedAt)
    public Journey(int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime,
                   String startLocation, String endLocation, String status, boolean dailyLimitExceeded) {
        this(0, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation,
             0, 0, 0, 0, 0, status, dailyLimitExceeded, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Journey(int id, int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime,
                   String startLocation, String endLocation, long totalDuration, long drivingDuration,
                   long breakDuration, long restDuration, long mealDuration, String status, boolean dailyLimitExceeded) {
        this(id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation,
             totalDuration, drivingDuration, breakDuration, restDuration, mealDuration, status, dailyLimitExceeded, null, null);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

    public long getTotalDuration() { return totalDuration; }
    public void setTotalDuration(long totalDuration) { this.totalDuration = totalDuration; }

    public long getDrivingDuration() { return drivingDuration; }
    public void setDrivingDuration(long drivingDuration) { this.drivingDuration = drivingDuration; }

    public long getBreakDuration() { return breakDuration; }
    public void setBreakDuration(long breakDuration) { this.breakDuration = breakDuration; }

    public long getRestDuration() { return restDuration; }
    public void setRestDuration(long restDuration) { this.restDuration = restDuration; }

    public long getMealDuration() { return mealDuration; }
    public void setMealDuration(long mealDuration) { this.mealDuration = mealDuration; }

    public String getStatus() { return status; } // NOVO GETTER
    public void setStatus(String status) { this.status = status; } // NOVO SETTER

    public boolean isDailyLimitExceeded() { return dailyLimitExceeded; } // NOVO GETTER
    public void setDailyLimitExceeded(boolean dailyLimitExceeded) { this.dailyLimitExceeded = dailyLimitExceeded; } // NOVO SETTER

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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
                ", totalDuration=" + totalDuration +
                ", drivingDuration=" + drivingDuration +
                ", breakDuration=" + breakDuration +
                ", restDuration=" + restDuration +
                ", mealDuration=" + mealDuration +
                ", status='" + status + '\'' +
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
                totalDuration == journey.totalDuration &&
                drivingDuration == journey.drivingDuration &&
                breakDuration == journey.breakDuration &&
                restDuration == journey.restDuration &&
                mealDuration == journey.mealDuration &&
                dailyLimitExceeded == journey.dailyLimitExceeded &&
                Objects.equals(journeyDate, journey.journeyDate) &&
                Objects.equals(startTime, journey.startTime) &&
                Objects.equals(endTime, journey.endTime) &&
                Objects.equals(startLocation, journey.startLocation) &&
                Objects.equals(endLocation, journey.endLocation) &&
                Objects.equals(status, journey.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation,
                totalDuration, drivingDuration, breakDuration, restDuration, mealDuration, status, dailyLimitExceeded);
    }
}
