// src/main/java/com/compliancesys/model/TimeRecord.java
package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.EventType;

/**
 * Representa um registro de tempo para uma jornada de motorista.
 * Corresponde à tabela 'time_records' no banco de dados.
 */
public class TimeRecord {
    private int id;
    private int driverId;
    private int journeyId;
    private LocalDateTime recordTime; // Renomeado de 'timestamp' para 'recordTime' para melhor clareza e alinhamento com o schema
    private EventType eventType;
    private String location;
    private Double latitude; // Adicionado
    private Double longitude; // Adicionado
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimeRecord() {
    }

    // Construtor completo com todos os campos, incluindo latitude e longitude
    public TimeRecord(int id, int driverId, int journeyId, LocalDateTime recordTime, EventType eventType,
                      String location, Double latitude, Double longitude,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.journeyId = journeyId;
        this.recordTime = recordTime;
        this.eventType = eventType;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para criação (sem ID, createdAt, updatedAt, latitude, longitude)
    // Este construtor é mais simples para a camada de serviço/controller ao criar um novo registro
    public TimeRecord(int driverId, int journeyId, LocalDateTime recordTime, EventType eventType, String location) {
        this(0, driverId, journeyId, recordTime, eventType, location, null, null, null, null);
    }

    // Construtor para criação (sem ID, createdAt, updatedAt, mas com latitude e longitude)
    public TimeRecord(int driverId, int journeyId, LocalDateTime recordTime, EventType eventType, String location,
                      Double latitude, Double longitude) {
        this(0, driverId, journeyId, recordTime, eventType, location, latitude, longitude, null, null);
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

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public LocalDateTime getRecordTime() { // Renomeado de getTimestamp()
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) { // Renomeado de setTimestamp()
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

    public Double getLatitude() { // Adicionado
        return latitude;
    }

    public void setLatitude(Double latitude) { // Adicionado
        this.latitude = latitude;
    }

    public Double getLongitude() { // Adicionado
        return longitude;
    }

    public void setLongitude(Double longitude) { // Adicionado
        this.longitude = longitude;
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
                ", journeyId=" + journeyId +
                ", recordTime=" + recordTime +
                ", eventType=" + eventType +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
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
                journeyId == that.journeyId &&
                Objects.equals(recordTime, that.recordTime) &&
                eventType == that.eventType &&
                Objects.equals(location, that.location) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, journeyId, recordTime, eventType, location, latitude, longitude);
    }
}
