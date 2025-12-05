package com.compliancesys.model;

import java.time.LocalDateTime; // Importa o enum EventType
import java.util.Objects;

import com.compliancesys.model.enums.EventType; // Adicionado para equals e hashCode

/**
 * Representa um registro de ponto de um motorista.
 * Corresponde à tabela 'time_records' no banco de dados.
 */
public class TimeRecord {
    private int id; // Renomeado de recordId para id para consistência
    private int driverId;
    private int vehicleId; // RE-ADICIONADO para alinhar com a lógica do JourneyServiceImpl
    private LocalDateTime recordTime; // Renomeado de timestamp para recordTime para clareza
    private EventType eventType; // Alterado para o enum EventType
    private String location; // Adicionado para detalhes de localização
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Adicionado para consistência com o schema e DAOs

    public TimeRecord() {
    }

    // Construtor completo
    public TimeRecord(int id, int driverId, int vehicleId, LocalDateTime recordTime, EventType eventType, String location, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId; // Inicializa vehicleId
        this.recordTime = recordTime;
        this.eventType = eventType;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public TimeRecord(int driverId, int vehicleId, LocalDateTime recordTime, EventType eventType, String location) {
        this(0, driverId, vehicleId, recordTime, eventType, location, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public TimeRecord(int id, int driverId, int vehicleId, LocalDateTime recordTime, EventType eventType, String location) {
        this(id, driverId, vehicleId, recordTime, eventType, location, null, null);
    }

    // Getters e Setters
    public int getId() { // Getter renomeado
        return id;
    }

    public void setId(int id) { // Setter renomeado
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() { // Getter RE-ADICIONADO
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) { // Setter RE-ADICIONADO
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getRecordTime() { // Getter renomeado
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) { // Setter renomeado
        this.recordTime = recordTime;
    }

    public EventType getEventType() { // Getter alterado para EventType
        return eventType;
    }

    public void setEventType(EventType eventType) { // Setter alterado para EventType
        this.eventType = eventType;
    }

    public String getLocation() { // Getter adicionado
        return location;
    }

    public void setLocation(String location) { // Setter adicionado
        this.location = location;
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
        return "TimeRecord{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", vehicleId=" + vehicleId + // Incluído no toString
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
                vehicleId == that.vehicleId && // Incluído no equals
                eventType == that.eventType &&
                Objects.equals(recordTime, that.recordTime) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, vehicleId, recordTime, eventType, location); // Incluído no hashCode
    }
}
