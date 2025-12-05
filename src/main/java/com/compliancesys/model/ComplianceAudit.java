package com.compliancesys.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.compliancesys.model.enums.ComplianceStatus;

/**
 * Representa um registro de auditoria de conformidade para uma jornada.
 * Corresponde à tabela 'compliance_audits' no banco de dados.
 */
public class ComplianceAudit {
    private int id;
    private int journeyId;
    private LocalDateTime auditDate;
    private ComplianceStatus complianceStatus; // Renomeado de 'status' para 'complianceStatus' para evitar conflitos
    private String auditorName; // ADICIONADO
    private String notes; // ADICIONADO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplianceAudit() {
    }

    // Construtor completo
    public ComplianceAudit(int id, int journeyId, LocalDateTime auditDate, ComplianceStatus complianceStatus, String auditorName, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
    public ComplianceAudit(int journeyId, LocalDateTime auditDate, ComplianceStatus complianceStatus, String auditorName, String notes) {
        this(0, journeyId, auditDate, complianceStatus, auditorName, notes, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public ComplianceAudit(int id, int journeyId, LocalDateTime auditDate, ComplianceStatus complianceStatus, String auditorName, String notes) {
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

    public ComplianceStatus getComplianceStatus() { // Getter para complianceStatus
        return complianceStatus;
    }

    public void setComplianceStatus(ComplianceStatus complianceStatus) { // Setter para complianceStatus
        this.complianceStatus = complianceStatus;
    }

    public String getAuditorName() { // ADICIONADO
        return auditorName;
    }

    public void setAuditorName(String auditorName) { // ADICIONADO
        this.auditorName = auditorName;
    }

    public String getNotes() { // ADICIONADO
        return notes;
    }

    public void setNotes(String notes) { // ADICIONADO
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
                ", complianceStatus=" + complianceStatus +
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
                complianceStatus == that.complianceStatus &&
                Objects.equals(auditDate, that.auditDate) &&
                Objects.equals(auditorName, that.auditorName) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, journeyId, auditDate, complianceStatus, auditorName, notes);
    }
}
