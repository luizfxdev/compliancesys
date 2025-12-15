// src/main/java/com/compliancesys/model/MobileCommunication.java
package com.compliancesys.model;

import java.time.LocalDateTime; // Importar o enum EventType
import java.util.Objects;

import com.compliancesys.model.enums.EventType;

/**
 * Representa um registro de comunicação móvel (localização, evento) de um motorista.
 * Corresponde à tabela 'mobile_communications' no banco de dados.
 */
public class MobileCommunication {
    private int id;
    private int driverId;
    private int journeyId; // ADICIONADO: Campo journeyId
    private LocalDateTime timestamp;
    private Double latitude;
    private Double longitude;
    private EventType eventType; // ADICIONADO: Campo eventType (enum)
    private String eventTypeString; // Para compatibilidade com o banco de dados (VARCHAR)
    private String deviceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MobileCommunication() {
    }

    // Construtor completo
    public MobileCommunication(int id, int driverId, int journeyId, LocalDateTime timestamp,
                               Double latitude, Double longitude, EventType eventType,
                               String deviceId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.journeyId = journeyId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventType = eventType;
        this.eventTypeString = eventType != null ? eventType.name() : null; // Sincroniza com a string
        this.deviceId = deviceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public MobileCommunication(int driverId, int journeyId, LocalDateTime timestamp,
                               Double latitude, Double longitude, EventType eventType, String deviceId) {
        this(0, driverId, journeyId, timestamp, latitude, longitude, eventType, deviceId, null, null);
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

    // ADICIONADO: Getter e Setter para journeyId
    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // ADICIONADO: Getter e Setter para eventType (enum)
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        this.eventTypeString = eventType != null ? eventType.name() : null; // Sincroniza com a string
    }

    // ADICIONADO: Getter e Setter para eventTypeString (para persistência)
    public String getEventTypeString() {
        return eventTypeString;
    }

    public void setEventTypeString(String eventTypeString) {
        this.eventTypeString = eventTypeString;
        try {
            this.eventType = eventTypeString != null ? EventType.valueOf(eventTypeString) : null;
        } catch (IllegalArgumentException e) {
            // Logar ou tratar o erro se a string não corresponder a um enum válido
            this.eventType = null; // Ou um valor padrão, como UNKNOWN
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileCommunication that = (MobileCommunication) o;
        return id == that.id &&
               driverId == that.driverId &&
               journeyId == that.journeyId && // Incluir journeyId no equals
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(latitude, that.latitude) &&
               Objects.equals(longitude, that.longitude) &&
               eventType == that.eventType && // Comparar enums diretamente
               Objects.equals(deviceId, that.deviceId) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, journeyId, timestamp, latitude, longitude, eventType,
                            deviceId, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "MobileCommunication{" +
               "id=" + id +
               ", driverId=" + driverId +
               ", journeyId=" + journeyId +
               ", timestamp=" + timestamp +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               ", eventType=" + eventType +
               ", deviceId='" + deviceId + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
