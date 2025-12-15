package com.compliancesys.model.enums;

public enum EventType {
    // Eventos principais usados no código
    START_DRIVE("Início de Direção"),
    END_DRIVE("Fim de Direção"),
    START_WORK("Início de Trabalho"),
    END_WORK("Fim de Trabalho"),
    ENTRY("Entrada"),
    EXIT("Saída"),
    REST("Descanso"),
    PAUSE("Pausa"),
    
    // Eventos adicionais
    START_JOURNEY("Início de Jornada"),
    END_JOURNEY("Fim de Jornada"),
    START_BREAK("Início de Descanso"),
    END_BREAK("Fim de Descanso"),
    START_MEAL("Início de Refeição"),
    END_MEAL("Fim de Refeição"),
    START_DRIVING("Início de Direção"),
    RESUME_DRIVING("Retomada de Direção"),
    END_DRIVING("Fim de Direção"),
    START_REST("Início de Descanso"),
    DRIVING("Dirigindo"),
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