package com.compliancesys.model;

import java.time.LocalDateTime; // Importa para usar LocalDateTime para o timestamp do evento.

/**
 * Representa um registro de ponto de um motorista.
 * Corresponde à tabela 'TimeRecord' no banco de dados.
 */
public class TimeRecord {
    private int recordId; // ID único do registro de ponto.
    private int driverId; // ID do motorista associado ao registro.
    private Integer vehicleId; // ID do veículo associado ao registro (pode ser nulo).
    private LocalDateTime eventTimestamp; // Data e hora exata do evento de ponto.
    private EventType eventType; // Tipo do evento de ponto (ex: INICIO_JORNADA).
    private String notes; // Notas adicionais sobre o registro.

    /**
     * Construtor padrão.
     */
    public TimeRecord() {
    }

    /**
     * Construtor com todos os campos.
     * @param recordId ID do registro.
     * @param driverId ID do motorista.
     * @param vehicleId ID do veículo.
     * @param eventTimestamp Timestamp do evento.
     * @param eventType Tipo do evento.
     * @param notes Notas.
     */
    public TimeRecord(int recordId, int driverId, Integer vehicleId, LocalDateTime eventTimestamp, EventType eventType, String notes) {
        this.recordId = recordId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.eventTimestamp = eventTimestamp;
        this.eventType = eventType;
        this.notes = notes;
    }

    // Getters e Setters para todos os campos.

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(LocalDateTime eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Retorna uma representação em String do objeto TimeRecord.
     * @return String formatada.
     */
    @Override
    public String toString() {
        return "TimeRecord{" +
               "recordId=" + recordId +
               ", driverId=" + driverId +
               ", vehicleId=" + vehicleId +
               ", eventTimestamp=" + eventTimestamp +
               ", eventType=" + eventType +
               ", notes='" + notes + '\'' +
               '}';
    }
}
