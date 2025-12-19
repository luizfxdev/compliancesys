package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.EventType;

public class MobileCommunication {
    private int id;
    private int driverId;
    private int journeyId;
    private LocalDateTime timestamp;
    private Double latitude;
    private Double longitude;
    private EventType eventType;
    private String eventTypeString;
    private String deviceId;
    private int signalStrength; // Adicionado
    private int batteryLevel;   // Adicionado
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MobileCommunication() {
    }

    public MobileCommunication(int id, int driverId, int journeyId, LocalDateTime timestamp,
                               Double latitude, Double longitude, EventType eventType,
                               String deviceId, int signalStrength, int batteryLevel, // Adicionado
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.journeyId = journeyId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventType = eventType;
        this.eventTypeString = eventType != null ? eventType.name() : null;
        this.deviceId = deviceId;
        this.signalStrength = signalStrength; // Adicionado
        this.batteryLevel = batteryLevel;     // Adicionado
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public MobileCommunication(int driverId, int journeyId, LocalDateTime timestamp,
                               Double latitude, Double longitude, EventType eventType,
                               String deviceId, int signalStrength, int batteryLevel) { // Adicionado
        this(0, driverId, journeyId, timestamp, latitude, longitude, eventType, deviceId, signalStrength, batteryLevel, null, null);
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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        this.eventTypeString = eventType != null ? eventType.name() : null;
    }

    public String getEventTypeString() {
        return eventTypeString;
    }

    public void setEventTypeString(String eventTypeString) {
        this.eventTypeString = eventTypeString;
        try {
            this.eventType = eventTypeString != null ? EventType.valueOf(eventTypeString) : null;
        } catch (IllegalArgumentException e) {
            this.eventType = null;
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getSignalStrength() { // Adicionado
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) { // Adicionado
        this.signalStrength = signalStrength;
    }

    public int getBatteryLevel() { // Adicionado
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) { // Adicionado
        this.batteryLevel = batteryLevel;
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
                journeyId == that.journeyId &&
                signalStrength == that.signalStrength && // Adicionado
                batteryLevel == that.batteryLevel &&     // Adicionado
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                eventType == that.eventType &&
                Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, journeyId, timestamp, latitude, longitude, eventType,
                deviceId, signalStrength, batteryLevel, createdAt, updatedAt); // Adicionado
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
                ", signalStrength=" + signalStrength + // Adicionado
                ", batteryLevel=" + batteryLevel +     // Adicionado
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
