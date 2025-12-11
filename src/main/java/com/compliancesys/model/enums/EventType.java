package com.compliancesys.model.enums;

public enum EventType {
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
    // Adicionando os estados que estavam faltando ou com nomes diferentes no JourneyServiceImpl
    DRIVING("Dirigindo"), // Estado genérico de direção
    REST("Descanso"),    // Estado genérico de descanso
    IDLE("Ocioso"),      // Estado de ociosidade
    OFF_DUTY("Fora de Serviço"); // Estado fora de serviço

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
