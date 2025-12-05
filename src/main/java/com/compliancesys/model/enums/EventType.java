package com.compliancesys.model.enums;

/**
 * Enumeração para os tipos de eventos de registro de ponto de um motorista.
 * Define os diferentes estados de uma jornada de trabalho.
 */
public enum EventType {
    IN("Entrada"),
    OUT("Saída"),
    START_JOURNEY("Início de Jornada"),
    END_JOURNEY("Fim de Jornada"),
    START_BREAK("Início de Pausa"),
    END_BREAK("Fim de Pausa"),
    START_MEAL("Início de Refeição"),
    END_MEAL("Fim de Refeição"),
    START_DRIVING("Início de Direção"),
    RESUME_DRIVING("Retomada de Direção"),
    END_DRIVING("Fim de Direção"),
    START_REST("Início de Descanso");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}