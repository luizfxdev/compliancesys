package com.compliancesys.model;

import java.time.LocalDateTime; // Importa para usar LocalDateTime para o timestamp de cálculo.

/**
 * Representa um registro de auditoria de conformidade.
 * Corresponde à tabela 'ComplianceAudit' no banco de dados.
 */
public class ComplianceAudit {
    private int auditId; // ID único da auditoria.
    private int journeyId; // ID da jornada auditada.
    private String violatedRule; // Regra da Lei do Caminhoneiro violada.
    private String alertDescription; // Descrição detalhada do alerta.
    private LocalDateTime calculationTimestamp; // Timestamp do cálculo da auditoria.
    private String alertJson; // JSON com detalhes adicionais do alerta.

    /**
     * Construtor padrão.
     */
    public ComplianceAudit() {
    }

    /**
     * Construtor com todos os campos.
     * @param auditId ID da auditoria.
     * @param journeyId ID da jornada.
     * @param violatedRule Regra violada.
     * @param alertDescription Descrição do alerta.
     * @param calculationTimestamp Timestamp do cálculo.
     * @param alertJson JSON do alerta.
     */
    public ComplianceAudit(int auditId, int journeyId, String violatedRule, String alertDescription, LocalDateTime calculationTimestamp, String alertJson) {
        this.auditId = auditId;
        this.journeyId = journeyId;
        this.violatedRule = violatedRule;
        this.alertDescription = alertDescription;
        this.calculationTimestamp = calculationTimestamp;
        this.alertJson = alertJson;
    }

    // Getters e Setters para todos os campos.

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public String getViolatedRule() {
        return violatedRule;
    }

    public void setViolatedRule(String violatedRule) {
        this.violatedRule = violatedRule;
    }

    public String getAlertDescription() {
        return alertDescription;
    }

    public void setAlertDescription(String alertDescription) {
        this.alertDescription = alertDescription;
    }

    public LocalDateTime getCalculationTimestamp() {
        return calculationTimestamp;
    }

    public void setCalculationTimestamp(LocalDateTime calculationTimestamp) {
        this.calculationTimestamp = calculationTimestamp;
    }

    public String getAlertJson() {
        return alertJson;
    }

    public void setAlertJson(String alertJson) {
        this.alertJson = alertJson;
    }

    /**
     * Retorna uma representação em String do objeto ComplianceAudit.
     * @return String formatada.
     */
    @Override
    public String toString() {
        return "ComplianceAudit{" +
               "auditId=" + auditId +
               ", journeyId=" + journeyId +
               ", violatedRule='" + violatedRule + '\'' +
               ", alertDescription='" + alertDescription + '\'' +
               ", calculationTimestamp=" + calculationTimestamp +
               ", alertJson='" + alertJson + '\'' +
               '}';
    }
}
