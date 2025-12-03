package com.compliancesys.model;

/**
 * Enum para tipos de eventos de registro de ponto.
 */
public enum EventType {
    INICIO_JORNADA("Início de Jornada"),
    FIM_JORNADA("Fim de Jornada"),
    INICIO_DESCANSO("Início de Descanso"),
    FIM_DESCANSO("Fim de Descanso"),
    INICIO_REFEICAO("Início de Refeição"),
    FIM_REFEICAO("Fim de Refeição"),
    ESPERA("Espera"),
    MOVIMENTACAO("Movimentação");

    private final String description; // Descrição legível do tipo de evento.

    /**
     * Construtor para EventType.
     * @param description Descrição do evento.
     */
    EventType(String description) {
        this.description = description;
    }

    /**
     * Retorna a descrição legível do tipo de evento.
     * @return Descrição do evento.
     */
    public String getDescription() {
        return description;
    }
}
