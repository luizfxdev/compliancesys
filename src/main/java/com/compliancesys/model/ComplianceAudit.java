package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro de auditoria de conformidade para uma jornada.
 * Corresponde à tabela 'compliance_audits' no banco de dados.
 * Alinhado com o schema.sql fornecido.
 */
public class ComplianceAudit {
    private int id;
    private int journeyId;
    private LocalDateTime auditDate;
    private String complianceStatus; // Alterado para String para alinhar com schema.sql
    private String auditorName;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplianceAudit() {
    }

    // Construtor completo
    public ComplianceAudit(int id, int journeyId, LocalDateTime auditDate, String complianceStatus,
                           String auditorName, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.journeyId = journeyId;
        this.auditDate = auditDate;
        this.complianceStatus = complianceStatus;
        this.auditorName = auditorName;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public ComplianceAudit(int journeyId, LocalDateTime auditDate, String complianceStatus,
                           String auditorName, String notes) {
        this(0, journeyId, auditDate, complianceStatus, auditorName, notes, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public ComplianceAudit(int id, int journeyId, LocalDateTime auditDate, String complianceStatus,
                           String auditorName, String notes) {
        this(id, journeyId, auditDate, complianceStatus, auditorName, notes, null, null);
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

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
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
                ", auditDate=" + auditDate +
                ", complianceStatus='" + complianceStatus + '\'' +
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
                Objects.equals(auditDate, that.auditDate) &&
                Objects.equals(complianceStatus, that.complianceStatus) &&
                Objects.equals(auditorName, that.auditorName) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, journeyId, auditDate, complianceStatus, auditorName, notes);
    }
}
