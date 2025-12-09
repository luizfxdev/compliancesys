package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.EventType;

/**
 * Representa um registro de ponto de um motorista.
 * Corresponde à tabela 'time_records' no banco de dados.
 */
public class TimeRecord {
    private int id;
    private int driverId;
    private int companyId; // ADICIONADO: Campo companyId
    private int vehicleId;
    private LocalDateTime recordTime;
    private EventType eventType;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimeRecord() {
    }

    // Construtor completo
    public TimeRecord(int id, int driverId, int companyId, int vehicleId, LocalDateTime recordTime, EventType eventType, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.companyId = companyId; // Inicializa companyId
        this.vehicleId = vehicleId;
        this.recordTime = recordTime;
        this.eventType = eventType;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public TimeRecord(int driverId, int companyId, int vehicleId, LocalDateTime recordTime, EventType eventType, String location) {
        this(0, driverId, companyId, vehicleId, recordTime, eventType, location, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public TimeRecord(int id, int driverId, int companyId, int vehicleId, LocalDateTime recordTime, EventType eventType, String location) {
        this(id, driverId, companyId, vehicleId, recordTime, eventType, location, null, null);
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

    // ADICIONADO: Getter para companyId
    public int getCompanyId() {
        return companyId;
    }

    // ADICIONADO: Setter para companyId
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        return "TimeRecord{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", companyId=" + companyId + // Incluído no toString
                ", vehicleId=" + vehicleId +
                ", recordTime=" + recordTime +
                ", eventType=" + eventType +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeRecord that = (TimeRecord) o;
        return id == that.id &&
                driverId == that.driverId &&
                companyId == that.companyId && // Incluído no equals
                vehicleId == that.vehicleId &&
                eventType == that.eventType &&
                Objects.equals(recordTime, that.recordTime) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, companyId, vehicleId, recordTime, eventType, location);
    }
}
