package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro de comunicação móvel de um motorista.
 * Corresponde à tabela 'mobile_communications' no banco de dados.
 */
public class MobileCommunication {
    private int id;
    private int driverId;
    private int recordId; // ID do registro de ponto associado
    private LocalDateTime timestamp;
    private Double latitude;
    private Double longitude;
    private LocalDateTime sendTimestamp; // ADICIONADO: Timestamp do envio da comunicação
    private boolean sendSuccess; // ADICIONADO: Indica se o envio foi bem-sucedido
    private String errorMessage; // ADICIONADO: Mensagem de erro, se houver
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MobileCommunication() {
    }

    // Construtor completo
    public MobileCommunication(int id, int driverId, int recordId, LocalDateTime timestamp, Double latitude, Double longitude, LocalDateTime sendTimestamp, boolean sendSuccess, String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.driverId = driverId;
        this.recordId = recordId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sendTimestamp = sendTimestamp; // Inicializa sendTimestamp
        this.sendSuccess = sendSuccess;     // Inicializa sendSuccess
        this.errorMessage = errorMessage;   // Inicializa errorMessage
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public MobileCommunication(int driverId, int recordId, LocalDateTime timestamp, Double latitude, Double longitude, LocalDateTime sendTimestamp, boolean sendSuccess, String errorMessage) {
        this(0, driverId, recordId, timestamp, latitude, longitude, sendTimestamp, sendSuccess, errorMessage, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public MobileCommunication(int id, int driverId, int recordId, LocalDateTime timestamp, Double latitude, Double longitude, LocalDateTime sendTimestamp, boolean sendSuccess, String errorMessage) {
        this(id, driverId, recordId, timestamp, latitude, longitude, sendTimestamp, sendSuccess, errorMessage, null, null);
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

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
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

    public LocalDateTime getSendTimestamp() { // ADICIONADO: Getter para sendTimestamp
        return sendTimestamp;
    }

    public void setSendTimestamp(LocalDateTime sendTimestamp) { // ADICIONADO: Setter para sendTimestamp
        this.sendTimestamp = sendTimestamp;
    }

    public boolean isSendSuccess() { // ADICIONADO: Getter para sendSuccess
        return sendSuccess;
    }

    public void setSendSuccess(boolean sendSuccess) { // ADICIONADO: Setter para sendSuccess
        this.sendSuccess = sendSuccess;
    }

    public String getErrorMessage() { // ADICIONADO: Getter para errorMessage
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) { // ADICIONADO: Setter para errorMessage
        this.errorMessage = errorMessage;
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
        return "MobileCommunication{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", recordId=" + recordId +
                ", timestamp=" + timestamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", sendTimestamp=" + sendTimestamp + // Incluído no toString
                ", sendSuccess=" + sendSuccess +     // Incluído no toString
                ", errorMessage='" + errorMessage + '\'' + // Incluído no toString
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileCommunication that = (MobileCommunication) o;
        return id == that.id &&
                driverId == that.driverId &&
                recordId == that.recordId &&
                sendSuccess == that.sendSuccess && // Incluído no equals
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(sendTimestamp, that.sendTimestamp) && // Incluído no equals
                Objects.equals(errorMessage, that.errorMessage); // Incluído no equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driverId, recordId, timestamp, latitude, longitude, sendTimestamp, sendSuccess, errorMessage); // Incluído no hashCode
    }
}
