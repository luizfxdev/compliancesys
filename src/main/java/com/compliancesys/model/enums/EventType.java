package com.compliancesys.model.enums;

public enum EventType {
    // Eventos principais usados no código
    START_DRIVE("Início de Direção"),
    END_DRIVE("Fim de Direção"),
    START_WORK("Início de Trabalho"),
    END_WORK("Fim de Trabalho"),
    ENTRY("Entrada"),
    EXIT("Saída"),
    REST("Descanso"), // Considerar se este é realmente necessário se tiver START_REST/END_REST
    PAUSE("Pausa"),

    // Eventos adicionais - Sugestão: Consolidar com os principais se houver redundância
    START_JOURNEY("Início de Jornada"),
    END_JOURNEY("Fim de Jornada"),
    START_BREAK("Início de Descanso"), // Pode ser consolidado com START_REST
    END_BREAK("Fim de Descanso"),     // Pode ser consolidado com END_REST
    START_MEAL("Início de Refeição"),
    END_MEAL("Fim de Refeição"),
    START_DRIVING("Início de Direção"), // Pode ser consolidado com START_DRIVE
    RESUME_DRIVING("Retomada de Direção"),
    END_DRIVING("Fim de Direção"),     // Pode ser consolidado com END_DRIVE
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
