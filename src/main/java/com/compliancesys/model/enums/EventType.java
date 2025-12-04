package com.compliancesys.model.enums; // Pacote corrigido para 'enums'

/**
 * Enumeração para os tipos de eventos de registro de ponto de um motorista.
 * Reflete os eventos da Lei 13.103/2015.
 */
public enum EventType {
    INICIO_JORNADA("Início de Jornada"),
    FIM_JORNADA("Fim de Jornada"),
    INICIO_DESCANSO("Início de Descanso"),
    FIM_DESCANSO("Fim de Descanso"),
    INICIO_REFEICAO("Início de Refeição"),
    FIM_REFEICAO("Fim de Refeição"),
    ESPERA("Espera"),
    MOVIMENTACAO("Movimentação"),
    // Adicionados para consistência com a lógica de jornada, se necessário
    INICIO_DIRECAO("Início de Direção"),
    FIM_DIRECAO("Fim de Direção");

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
