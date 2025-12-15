package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.compliancesys.model.TimeRecord;

/**
 * Interface para utilitários de manipulação de tempo e cálculos de jornada,
 * especialmente para as regras da Lei do Caminhoneiro (Lei 13.103/2015).
 */
public interface TimeUtil {
    /**
     * Calcula a duração total de trabalho a partir de uma lista de registros de ponto.
     */
    Duration calculateTotalWorkDuration(List<TimeRecord> timeRecords);

    /**
     * Calcula a duração total de descanso a partir de uma lista de registros de ponto.
     */
    Duration calculateTotalRestDuration(List<TimeRecord> timeRecords);

    /**
     * Verifica se a duração de trabalho contínuo excede o limite permitido.
     * Retorna true se exceder, false caso contrário.
     */
    boolean exceedsMaxContinuousDriving(List<TimeRecord> timeRecords, Duration maxContinuousDriving);

    /**
     * Verifica se o motorista teve o tempo mínimo de descanso interjornada.
     */
    boolean hasInsufficientInterJourneyRest(LocalDateTime lastExit, LocalDateTime nextEntry, Duration minInterJourneyRest);

    /**
     * Verifica se o motorista teve o tempo mínimo de descanso intrajornada.
     */
    boolean hasInsufficientIntraJourneyRest(List<TimeRecord> timeRecords, Duration minIntraJourneyRest, Duration maxDrivingBeforeRest);

    /**
     * Calcula a duração de um período entre dois LocalDateTime.
     */
    Duration calculateDuration(LocalDateTime start, LocalDateTime end);

    /**
     * Calcula a duração máxima de direção contínua nos registros fornecidos.
     */
    Duration calculateMaxContinuousDriving(List<TimeRecord> timeRecords);

    /**
     * Verifica se há período de descanso adequado após direção.
     */
    boolean hasRestPeriodAfterDriving(List<TimeRecord> timeRecords, Duration maxDrivingDuration);
}