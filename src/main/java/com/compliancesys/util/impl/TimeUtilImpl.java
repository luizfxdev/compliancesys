package com.compliancesys.util.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.compliancesys.model.TimeRecord; // Garanta que esta importação está correta
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.TimeUtil;

/**
 * Implementação de TimeUtil para cálculos de tempo e validações de jornada
 * conforme as regras da Lei do Caminhoneiro (Lei 13.103/2015).
 */
public class TimeUtilImpl implements TimeUtil {
    private static final Logger LOGGER = Logger.getLogger(TimeUtilImpl.class.getName());

    @Override
    public Duration calculateTotalWorkDuration(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return Duration.ZERO;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime)) // CORREÇÃO: Usando getRecordTime()
                .collect(Collectors.toList());

        Duration totalWork = Duration.ZERO;
        LocalDateTime startWork = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_WORK || record.getEventType() == EventType.START_DRIVE) {
                if (startWork == null) {
                    startWork = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
                }
            } else if (record.getEventType() == EventType.END_WORK || record.getEventType() == EventType.END_DRIVE) {
                if (startWork != null) {
                    totalWork = totalWork.plus(Duration.between(startWork, record.getRecordTime())); // CORREÇÃO: Usando getRecordTime()
                    startWork = null; // Reset para o próximo período de trabalho
                }
            }
        }
        // Se o trabalho começou mas não terminou, consideramos até o último registro
        if (startWork != null && !sortedRecords.isEmpty()) {
            totalWork = totalWork.plus(Duration.between(startWork, sortedRecords.get(sortedRecords.size() - 1).getRecordTime())); // CORREÇÃO: Usando getRecordTime()
        }
        return totalWork;
    }

    @Override
    public Duration calculateTotalRestDuration(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return Duration.ZERO;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime)) // CORREÇÃO: Usando getRecordTime()
                .collect(Collectors.toList());

        Duration totalRest = Duration.ZERO;
        LocalDateTime startRest = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_REST) {
                if (startRest == null) {
                    startRest = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
                }
            } else if (record.getEventType() == EventType.END_REST) {
                if (startRest != null) {
                    totalRest = totalRest.plus(Duration.between(startRest, record.getRecordTime())); // CORREÇÃO: Usando getRecordTime()
                    startRest = null; // Reset para o próximo período de descanso
                }
            }
        }
        // Se o descanso começou mas não terminou, consideramos até o último registro
        if (startRest != null && !sortedRecords.isEmpty()) {
            totalRest = totalRest.plus(Duration.between(startRest, sortedRecords.get(sortedRecords.size() - 1).getRecordTime())); // CORREÇÃO: Usando getRecordTime()
        }
        return totalRest;
    }

    @Override
    public boolean exceedsMaxContinuousDriving(List<TimeRecord> timeRecords, Duration maxContinuousDriving) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return false;
        }

        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime)) // CORREÇÃO: Usando getRecordTime()
                .collect(Collectors.toList());

        Duration currentDrivingSegment = Duration.ZERO;
        LocalDateTime lastEntry = null; // Último recordTime de um evento de direção
        EventType lastEventType = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_DRIVE) {
                lastEntry = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
                lastEventType = EventType.START_DRIVE;
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastEntry != null && lastEventType == EventType.START_DRIVE) {
                    currentDrivingSegment = currentDrivingSegment.plus(Duration.between(lastEntry, record.getRecordTime())); // CORREÇÃO: Usando getRecordTime()
                    if (currentDrivingSegment.compareTo(maxContinuousDriving) > 0) {
                        return true;
                    }
                    lastEntry = null; // Reinicia o segmento de direção
                    lastEventType = null;
                }
            } else if (record.getEventType() == EventType.START_REST || record.getEventType() == EventType.END_REST || record.getEventType() == EventType.START_WORK || record.getEventType() == EventType.END_WORK) {
                // Qualquer evento que não seja de direção, reseta o contador de direção contínua
                currentDrivingSegment = Duration.ZERO;
                lastEntry = null;
                lastEventType = null;
            }
        }
        // Se a direção começou mas não terminou, e o último evento foi START_DRIVE,
        // e não houve um END_DRIVE correspondente, o segmento de direção continua até o final da lista
        if (lastEntry != null && lastEventType == EventType.START_DRIVE && !sortedRecords.isEmpty()) {
            currentDrivingSegment = currentDrivingSegment.plus(Duration.between(lastEntry, sortedRecords.get(sortedRecords.size() - 1).getRecordTime())); // CORREÇÃO: Usando getRecordTime()
            if (currentDrivingSegment.compareTo(maxContinuousDriving) > 0) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean hasInsufficientInterJourneyRest(LocalDateTime lastExit, LocalDateTime nextEntry, Duration minInterJourneyRest) {
        if (lastExit == null || nextEntry == null) {
            return false; // Não é possível verificar se faltam dados
        }
        Duration restDuration = Duration.between(lastExit, nextEntry);
        return restDuration.compareTo(minInterJourneyRest) < 0;
    }

    @Override
    public boolean hasInsufficientIntraJourneyRest(List<TimeRecord> timeRecords, Duration minIntraJourneyRest, Duration maxDrivingBeforeRest) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return false;
        }

        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime)) // CORREÇÃO: Usando getRecordTime()
                .collect(Collectors.toList());

        Duration drivingSinceLastRest = Duration.ZERO;
        LocalDateTime lastDrivingStart = null;
        LocalDateTime lastRestEnd = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_DRIVE) {
                lastDrivingStart = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastDrivingStart != null) {
                    drivingSinceLastRest = drivingSinceLastRest.plus(Duration.between(lastDrivingStart, record.getRecordTime())); // CORREÇÃO: Usando getRecordTime()
                    lastDrivingStart = null; // Reset
                }
            } else if (record.getEventType() == EventType.START_REST) {
                // Se um descanso começa, zera o contador de direção
                drivingSinceLastRest = Duration.ZERO;
                lastRestEnd = null; // O descanso ainda não terminou
            } else if (record.getEventType() == EventType.END_REST) {
                lastRestEnd = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
            }

            // Verifica se a direção acumulada excede o máximo sem descanso
            if (drivingSinceLastRest.compareTo(maxDrivingBeforeRest) > 0) {
                // Se excedeu, precisamos verificar se houve um descanso adequado
                // Isso é uma simplificação, a lógica real pode ser mais complexa
                // Por exemplo, verificar se o descanso foi de pelo menos minIntraJourneyRest
                if (lastRestEnd == null || Duration.between(lastRestEnd, record.getRecordTime()).compareTo(minIntraJourneyRest) < 0) { // CORREÇÃO: Usando getRecordTime()
                    return true; // Direção excessiva sem descanso adequado
                }
            }
        }
        return false;
    }

    @Override
    public Duration calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || end.isBefore(start)) {
            return Duration.ZERO;
        }
        return Duration.between(start, end);
    }

    @Override
    public Duration calculateMaxContinuousDriving(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return Duration.ZERO;
        }

        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime)) // CORREÇÃO: Usando getRecordTime()
                .collect(Collectors.toList());

        Duration maxContinuous = Duration.ZERO;
        Duration currentContinuous = Duration.ZERO;
        LocalDateTime lastDrivingStart = null;
        LocalDateTime lastEventRecordTime = null; // CORREÇÃO: Renomeado para lastEventRecordTime

        for (TimeRecord record : sortedRecords) {
            if (lastEventRecordTime != null) { // CORREÇÃO: Usando lastEventRecordTime
                // Se houve um gap entre eventos, e o último foi START_DRIVE,
                // e o gap não foi um descanso, então a direção contínua pode ter sido interrompida
                // ou continuado implicitamente. Para simplificar, consideramos interrupção.
                if (record.getRecordTime().isAfter(lastEventRecordTime) && // CORREÇÃO: Usando getRecordTime() e lastEventRecordTime
                    (record.getEventType() == EventType.START_REST || record.getEventType() == EventType.END_REST ||
                     record.getEventType() == EventType.START_WORK || record.getEventType() == EventType.END_WORK)) {
                    // Se o evento atual é um descanso ou trabalho, zera a contagem de direção contínua
                    currentContinuous = Duration.ZERO;
                    lastDrivingStart = null;
                }
            }

            if (record.getEventType() == EventType.START_DRIVE) {
                lastDrivingStart = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastDrivingStart != null) {
                    currentContinuous = currentContinuous.plus(Duration.between(lastDrivingStart, record.getRecordTime())); // CORREÇÃO: Usando getRecordTime()
                    if (currentContinuous.compareTo(maxContinuous) > 0) {
                        maxContinuous = currentContinuous;
                    }
                    currentContinuous = Duration.ZERO; // Reinicia após um END_DRIVE
                    lastDrivingStart = null;
                }
            }
            lastEventRecordTime = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
        }

        // Se a direção começou mas não terminou (último evento foi START_DRIVE)
        if (lastDrivingStart != null && !sortedRecords.isEmpty()) {
            currentContinuous = currentContinuous.plus(Duration.between(lastDrivingStart, sortedRecords.get(sortedRecords.size() - 1).getRecordTime())); // CORREÇÃO: Usando getRecordTime()
            if (currentContinuous.compareTo(maxContinuous) > 0) {
                maxContinuous = currentContinuous;
            }
        }

        return maxContinuous;
    }

    @Override
    public boolean hasRestPeriodAfterDriving(List<TimeRecord> timeRecords, Duration maxDrivingDuration) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return true; // Não há direção, então não há problema
        }

        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime)) // CORREÇÃO: Usando getRecordTime()
                .collect(Collectors.toList());

        Duration currentDriving = Duration.ZERO;
        LocalDateTime lastDrivingStart = null;
        LocalDateTime lastDrivingEnd = null; // Último END_DRIVE
        LocalDateTime lastRestEnd = null; // Último END_REST

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_DRIVE) {
                lastDrivingStart = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastDrivingStart != null) {
                    currentDriving = currentDriving.plus(Duration.between(lastDrivingStart, record.getRecordTime())); // CORREÇÃO: Usando getRecordTime()
                    lastDrivingEnd = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
                    lastDrivingStart = null;
                }
            } else if (record.getEventType() == EventType.END_REST) {
                lastRestEnd = record.getRecordTime(); // CORREÇÃO: Usando getRecordTime()
                currentDriving = Duration.ZERO; // Descanso zera a contagem de direção
            }

            // Se a direção acumulada excede o máximo e não houve descanso adequado depois
            if (currentDriving.compareTo(maxDrivingDuration) > 0) {
                // Se não houve um descanso após o último período de direção que excedeu o limite
                if (lastDrivingEnd != null && (lastRestEnd == null || lastRestEnd.isBefore(lastDrivingEnd))) {
                    return false; // Direção excessiva sem descanso adequado
                }
            }
        }
        return true;
    }
}
