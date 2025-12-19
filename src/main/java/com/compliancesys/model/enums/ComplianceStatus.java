package com.compliancesys.model.enums;

/**
 * Enumeração para o status de conformidade de uma jornada de trabalho.
 */
public enum ComplianceStatus {
    CONFORME("Conforme"),
    NAO_CONFORME("Não Conforme"),
    ALERTA("Alerta"),
    PENDING("Pendente"), // Adicionado para resolver o erro de "cannot find symbol variable PENDENTE"
    EM_ANDAMENTO("Em Andamento"),
    INVALIDO("Inválido"),
    COMPLIANT("Compliant"),
    NON_COMPLIANT("Non-Compliant"),
    UNKNOWN("Desconhecido");

    private final String description;

    /**
     * Construtor para ComplianceStatus.
     * @param description Descrição do status.
     */
    ComplianceStatus(String description) {
        this.description = description;
    }

    /**
     * Retorna a descrição do status.
     * @return A descrição do status.
     */
    public String getDescription() {
        return description;
    }
}
