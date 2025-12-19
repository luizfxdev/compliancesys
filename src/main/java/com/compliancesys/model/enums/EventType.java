package com.compliancesys.model.enums;

public enum EventType {
    // Eventos de Jornada e Direção
    START_JOURNEY("Início de Jornada"),
    END_JOURNEY("Fim de Jornada"),
    START_DRIVE("Início de Direção"),
    END_DRIVE("Fim de Direção"),
    DRIVING("Dirigindo"),
    RESUME_DRIVING("Retomada de Direção"),

    // Eventos de Trabalho e Descanso
    START_WORK("Início de Trabalho"),
    END_WORK("Fim de Trabalho"),
    START_REST("Início de Descanso"),
    END_REST("Fim de Descanso"), 
    REST("Descanso"),
    START_BREAK("Início de Pausa"), 
    END_BREAK("Fim de Pausa"),     
    START_MEAL("Início de Refeição"),
    END_MEAL("Fim de Refeição"),

    // Outros Eventos
    ENTRY("Entrada"),
    EXIT("Saída"),
    PAUSE("Pausa"),
    IDLE("Ocioso"),
    OFF_DUTY("Fora de Serviço");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
