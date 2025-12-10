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
    private int vehicleId; // Adicionado para associar a jornada a um veículo
    private LocalDate journeyDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startLocation;
    private String endLocation;
    private double totalDistance; // Em km
    private double totalDuration; // Em minutos
    private double drivingDuration; // Em minutos
    private double breakDuration; // Em minutos
    private double restDuration; // Em minutos
    private double mealDuration; // Em minutos
    private String status; // Mantido como String, conforme sua correção
    private boolean dailyLimitExceeded; // Indica se algum limite diário foi excedido
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Journey() {
    }

    // Construtor completo
    public Journey(int id, int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime,
                   String startLocation, String endLocation, double totalDistance, double totalDuration,
                   double drivingDuration, double breakDuration, double restDuration, double mealDuration,
                   String status, boolean dailyLimitExceeded, LocalDateTime createdAt, LocalDateTime updatedAt) { // Status como String
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.journeyDate = journeyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.totalDistance = totalDistance;
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

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public Journey(int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime,
                   String startLocation, String endLocation, double totalDistance, double totalDuration,
                   double drivingDuration, double breakDuration, double restDuration, double mealDuration,
                   String status, boolean dailyLimitExceeded) { // Status como String
        this(0, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation,
             totalDistance, totalDuration, drivingDuration, breakDuration, restDuration, mealDuration,
             status, dailyLimitExceeded, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public Journey(int id, int driverId, int vehicleId, LocalDate journeyDate, LocalDateTime startTime, LocalDateTime endTime,
                   String startLocation, String endLocation, double totalDistance, double totalDuration,
                   double drivingDuration, double breakDuration, double restDuration, double mealDuration,
                   String status, boolean dailyLimitExceeded) { // Status como String
        this(id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation,
             totalDistance, totalDuration, drivingDuration, breakDuration, restDuration, mealDuration,
             status, dailyLimitExceeded, null, null);
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

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getDrivingDuration() {
        return drivingDuration;
    }

    public void setDrivingDuration(double drivingDuration) {
        this.drivingDuration = drivingDuration;
    }

    public double getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(double breakDuration) {
        this.breakDuration = breakDuration;
    }

    public double getRestDuration() {
        return restDuration;
    }

    public void setRestDuration(double restDuration) {
        this.restDuration = restDuration;
    }

    public double getMealDuration() {
        return mealDuration;
    }

    public void setMealDuration(double mealDuration) {
        this.mealDuration = mealDuration;
    }

    public String getStatus() { // Retorna String
        return status;
    }

    public void setStatus(String status) { // Recebe String
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
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", drivingDuration=" + drivingDuration +
                ", breakDuration=" + breakDuration +
                ", restDuration=" + restDuration +
                ", mealDuration=" + mealDuration +
                ", status='" + status + '\'' + // Mantido como String
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
                Double.compare(journey.totalDistance, totalDistance) == 0 &&
                Double.compare(journey.totalDuration, totalDuration) == 0 &&
                Double.compare(journey.drivingDuration, drivingDuration) == 0 &&
                Double.compare(journey.breakDuration, breakDuration) == 0 &&
                Double.compare(journey.restDuration, restDuration) == 0 &&
                Double.compare(journey.mealDuration, mealDuration) == 0 &&
                dailyLimitExceeded == journey.dailyLimitExceeded &&
                Objects.equals(journeyDate, journey.journeyDate) &&
                Objects.equals(startTime, journey.startTime) &&
                Objects.equals(endTime, journey.endTime) &&
                Objects.equals(startLocation, journey.startLocation) &&
                Objects.equals(endLocation, journey.endLocation) &&
                Objects.equals(status, journey.status) && // Comparação de String
                Objects.equals(createdAt, journey.createdAt) &&
                Objects.equals(updatedAt, journey.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, vehicleId, journeyDate, startTime, endTime, startLocation, endLocation,
                            totalDistance, totalDuration, drivingDuration, breakDuration, restDuration, mealDuration,
                            status, dailyLimitExceeded, createdAt, updatedAt);
    }
}
