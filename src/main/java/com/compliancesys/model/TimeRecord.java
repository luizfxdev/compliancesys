package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.EventType;

public class TimeRecord {
    private int id;
    private int driverId;
    private int journeyId;
    private LocalDateTime recordTime;
    private EventType eventType;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimeRecord() {
    }

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

    public TimeRecord(int driverId, int journeyId, LocalDateTime recordTime, EventType eventType, String location) {
        this(0, driverId, journeyId, recordTime, eventType, location, null, null, null, null);
    }

    public TimeRecord(int driverId, int journeyId, LocalDateTime recordTime, EventType eventType, String location,
                      Double latitude, Double longitude) {
        this(0, driverId, journeyId, recordTime, eventType, location, latitude, longitude, null, null);
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
