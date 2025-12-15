// src/main/java/com/compliancesys/model/ComplianceAudit.java
package com.compliancesys.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.ComplianceStatus;

/**
 * Representa um registro de auditoria de conformidade para uma jornada.
 * Corresponde à tabela 'compliance_audits' no banco de dados.
 * Alinhado com o schema.sql fornecido e estendido para suportar regras da Lei do Caminhoneiro.
 */
public class ComplianceAudit {
    private int id;
    private int journeyId;
    private int driverId; // Adicionado para consistência, embora não esteja diretamente no schema.sql para esta tabela, é comum em auditorias.
    private LocalDate auditDate;
    private LocalDateTime auditTimestamp;
    private ComplianceStatus status; // Alterado para enum ComplianceStatus
    private String complianceStatus; // Mantido para compatibilidade com schema.sql (representação em String do enum)
    private String violations;
    private Duration totalWorkDuration;
    private Duration maxContinuousDriving;
    private String auditorName;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplianceAudit() {
    }

    // Construtor completo
    public ComplianceAudit(int id, int journeyId, int driverId, LocalDate auditDate,
                           LocalDateTime auditTimestamp, ComplianceStatus status,
                           String violations, Duration totalWorkDuration,
                           Duration maxContinuousDriving, String auditorName,
                           String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.journeyId = journeyId;
        this.driverId = driverId;
        this.auditDate = auditDate;
        this.auditTimestamp = auditTimestamp;
        this.status = status;
        this.complianceStatus = status != null ? status.name() : null; // Garante que a string seja atualizada
        this.violations = violations;
        this.totalWorkDuration = totalWorkDuration;
        this.maxContinuousDriving = maxContinuousDriving;
        this.auditorName = auditorName;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt, e com campos de Duration e violations nulos inicialmente)
    public ComplianceAudit(int journeyId, int driverId, LocalDate auditDate,
                           ComplianceStatus status, String auditorName, String notes) {
        this(0, journeyId, driverId, auditDate, null, status, null, null, null,
             auditorName, notes, null, null);
    }

    // Construtor com timestamp e violations (útil para criação inicial de auditoria)
    public ComplianceAudit(int journeyId, int driverId, LocalDateTime auditTimestamp,
                           ComplianceStatus status, String violations) {
        this(0, journeyId, driverId, auditTimestamp != null ? auditTimestamp.toLocalDate() : null,
             auditTimestamp, status, violations, null, null, null, null, null, null);
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public LocalDate getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }

    public LocalDateTime getAuditTimestamp() {
        return auditTimestamp;
    }

    public void setAuditTimestamp(LocalDateTime auditTimestamp) {
        this.auditTimestamp = auditTimestamp;
        if (auditTimestamp != null) {
            this.auditDate = auditTimestamp.toLocalDate();
        }
    }

    public ComplianceStatus getStatus() {
        return status;
    }

    public void setStatus(ComplianceStatus status) {
        this.status = status;
        this.complianceStatus = status != null ? status.name() : null; // Garante que a string seja atualizada
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
        try {
            this.status = complianceStatus != null ? ComplianceStatus.valueOf(complianceStatus) : null;
        } catch (IllegalArgumentException e) {
            // Logar ou tratar o erro se a string não corresponder a um enum válido
            this.status = null; // Ou um valor padrão, como UNKNOWN
        }
    }

    public String getViolations() {
        return violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }

    public Duration getTotalWorkDuration() {
        return totalWorkDuration;
    }

    public void setTotalWorkDuration(Duration totalWorkDuration) {
        this.totalWorkDuration = totalWorkDuration;
    }

    public Duration getMaxContinuousDriving() {
        return maxContinuousDriving;
    }

    public void setMaxContinuousDriving(Duration maxContinuousDriving) {
        this.maxContinuousDriving = maxContinuousDriving;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        return "ComplianceAudit{" +
                "id=" + id +
                ", journeyId=" + journeyId +
                ", driverId=" + driverId +
                ", auditDate=" + auditDate +
                ", auditTimestamp=" + auditTimestamp +
                ", status=" + status +
                ", violations='" + violations + '\'' +
                ", totalWorkDuration=" + totalWorkDuration +
                ", maxContinuousDriving=" + maxContinuousDriving +
                ", auditorName='" + auditorName + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceAudit that = (ComplianceAudit) o;
        return id == that.id &&
                journeyId == that.journeyId &&
                driverId == that.driverId &&
                Objects.equals(auditDate, that.auditDate) &&
                Objects.equals(auditTimestamp, that.auditTimestamp) &&
                status == that.status &&
                Objects.equals(violations, that.violations) &&
                Objects.equals(totalWorkDuration, that.totalWorkDuration) &&
                Objects.equals(maxContinuousDriving, that.maxContinuousDriving) &&
                Objects.equals(auditorName, that.auditorName) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, journeyId, driverId, auditDate, auditTimestamp, status,
                violations, totalWorkDuration, maxContinuousDriving,
                auditorName, notes);
    }
}
