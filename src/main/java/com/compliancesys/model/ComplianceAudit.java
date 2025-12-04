package com.compliancesys.model;

import com.compliancesys.model.enums.ComplianceStatus; // Importa o enum ComplianceStatus
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro de auditoria de conformidade para uma jornada.
 * Corresponde à tabela 'compliance_audits' no banco de dados.
 */
public class ComplianceAudit {
    private int id;
    private int journeyId;
    private LocalDateTime auditDate; // Renomeado de timestamp para auditDate para clareza
    private ComplianceStatus status; // Alterado para o enum ComplianceStatus
    private String details;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Adicionado para consistência com o schema e DAOs

    public ComplianceAudit() {
    }

    // Construtor completo
    public ComplianceAudit(int id, int journeyId, LocalDateTime auditDate, ComplianceStatus status, String details, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.journeyId = journeyId;
        this.auditDate = auditDate;
        this.status = status;
        this.details = details;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Construtor para inserção (sem ID, createdAt, updatedAt)
    public ComplianceAudit(int journeyId, LocalDateTime auditDate, ComplianceStatus status, String details) {
        this(0, journeyId, auditDate, status, details, null, null);
    }

    // Construtor para atualização (com ID, sem createdAt, updatedAt)
    public ComplianceAudit(int id, int journeyId, LocalDateTime auditDate, ComplianceStatus status, String details) {
        this(id, journeyId, auditDate, status, details, null, null);
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

    public LocalDateTime getAuditDate() { // Getter renomeado
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) { // Setter renomeado
        this.auditDate = auditDate;
    }

    public ComplianceStatus getStatus() { // Getter alterado para ComplianceStatus
        return status;
    }

    public void setStatus(ComplianceStatus status) { // Setter alterado para ComplianceStatus
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() { // Getter adicionado
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) { // Setter adicionado
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ComplianceAudit{" +
                "id=" + id +
                ", journeyId=" + journeyId +
                ", auditDate=" + auditDate +
                ", status=" + status +
                ", details='" + details + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceAudit that = (ComplianceAudit) o;
        return id == that.id && journeyId == that.journeyId && Objects.equals(auditDate, that.auditDate) && status == that.status && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, journeyId, auditDate, status, details);
    }
}
