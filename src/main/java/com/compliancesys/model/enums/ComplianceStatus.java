package com.compliancesys.model.enums; // Pacote corrigido para 'enums'

/**
 * Enumeração para o status de conformidade de uma jornada de trabalho.
 */
public enum ComplianceStatus {
    CONFORME("Conforme"),
    NAO_CONFORME("Não Conforme"),
    ALERTA("Alerta"),
    PENDING("Pendente"), // Renomeado de PENDENTE para PENDING para consistência com o código
    EM_ANDAMENTO("Em Andamento"),
    INVALIDO("Inválido"); // Adicionado para cobrir cenários de dados inválidos

    private final String description; // Descrição legível do status de conformidade.

    /**
     * Construtor para ComplianceStatus.
     * @param description Descrição do status.
     */
    ComplianceStatus(String description) {
        this.description = description;
    }

    /**
     * Retorna a descrição legível do status de conformidade.
     * @return Descrição do status.
     */
    public String getDescription() {
        return description;
    }
}
