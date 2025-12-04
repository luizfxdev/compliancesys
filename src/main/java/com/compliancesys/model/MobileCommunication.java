package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro de comunicação móvel associado a um registro de ponto.
 * Corresponde à tabela 'mobile_communications' no banco de dados.
 */
public class MobileCommunication {
    private int id; // Renomeado de id para id para consistência
    private int recordId; // ID do TimeRecord ao qual esta comunicação está associada
    private LocalDateTime sendTimestamp;
    private boolean sendSuccess;
    private String errorMessage; // Para registrar erros de envio
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Adicionado para consistência com o schema e DAOs

    // Construtor completo
    public MobileCommunication(int id, int recordId, LocalDateTime sendTimestamp, boolean sendSuccess, String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.recordId = recordId;
        this.sendTimestamp = sendTimestamp;
        this.sendSuccess = sendSuccess;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public MobileCommunication(int recordId, LocalDateTime sendTimestamp, boolean sendSuccess, String errorMessage) {
        this(0, recordId, sendTimestamp, sendSuccess, errorMessage, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public MobileCommunication(int id, int recordId, LocalDateTime sendTimestamp, boolean sendSuccess, String errorMessage) {
        this(id, recordId, sendTimestamp, sendSuccess, errorMessage, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public LocalDateTime getSendTimestamp() {
        return sendTimestamp;
    }

    public void setSendTimestamp(LocalDateTime sendTimestamp) {
        this.sendTimestamp = sendTimestamp;
    }

    public boolean isSendSuccess() {
        return sendSuccess;
    }

    public void setSendSuccess(boolean sendSuccess) {
        this.sendSuccess = sendSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileCommunication that = (MobileCommunication) o;
        return id == that.id && recordId == that.recordId && sendSuccess == that.sendSuccess && Objects.equals(sendTimestamp, that.sendTimestamp) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recordId, sendTimestamp, sendSuccess, errorMessage);
    }

    @Override
    public String toString() {
        return "MobileCommunication{" +
                "id=" + id +
                ", recordId=" + recordId +
                ", sendTimestamp=" + sendTimestamp +
                ", sendSuccess=" + sendSuccess +
                ", errorMessage='" + errorMessage + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
