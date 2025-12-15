package com.compliancesys.util.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.compliancesys.model.TimeRecord;
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
                .sorted(Comparator.comparing(TimeRecord::getTimestamp))
                .collect(Collectors.toList());
        Duration totalWork = Duration.ZERO;
        LocalDateTime entryTime = null;
        for (TimeRecord record : sortedRecords) {
            EventType eventType = record.getEventType();
            if (eventType == EventType.ENTRY || eventType == EventType.START_DRIVE) {
                if (entryTime == null) {
                    entryTime = record.getTimestamp();
                }
            } else if ((eventType == EventType.EXIT || eventType == EventType.END_DRIVE) && entryTime != null) {
                totalWork = totalWork.plus(Duration.between(entryTime, record.getTimestamp()));
                entryTime = null;
            }
        }
        LOGGER.log(Level.FINE, "Duração total de trabalho calculada: {0}", totalWork);
        return totalWork;
    }

    @Override
    public Duration calculateTotalRestDuration(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.size() < 2) {
            return Duration.ZERO;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getTimestamp))
                .collect(Collectors.toList());
        Duration totalRest = Duration.ZERO;
        LocalDateTime lastExitTime = null;
        for (TimeRecord currentRecord : sortedRecords) {
            EventType eventType = currentRecord.getEventType();
            if (eventType == EventType.EXIT || eventType == EventType.END_DRIVE) {
                lastExitTime = currentRecord.getTimestamp();
            } else if ((eventType == EventType.ENTRY || eventType == EventType.START_DRIVE) && lastExitTime != null) {
                totalRest = totalRest.plus(Duration.between(lastExitTime, currentRecord.getTimestamp()));
                lastExitTime = null;
            }
        }
        LOGGER.log(Level.FINE, "Duração total de descanso calculada: {0}", totalRest);
        return totalRest;
    }

    @Override
    public boolean exceedsMaxContinuousDriving(List<TimeRecord> timeRecords, Duration maxContinuousDriving) {
        if (timeRecords == null || timeRecords.isEmpty() || maxContinuousDriving == null || maxContinuousDriving.isNegative()) {
            return false;
        }
        Duration maxFound = calculateMaxContinuousDriving(timeRecords);
        boolean exceeds = maxFound.compareTo(maxContinuousDriving) > 0;
        if (exceeds) {
            LOGGER.log(Level.WARNING, "Excedeu o tempo máximo de direção contínua: {0} (limite: {1})",
                    new Object[]{maxFound, maxContinuousDriving});
        }
        return exceeds;
    }

    @Override
    public Duration calculateMaxContinuousDriving(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return Duration.ZERO;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getTimestamp))
                .collect(Collectors.toList());
        Duration maxContinuous = Duration.ZERO;
        Duration currentContinuous = Duration.ZERO;
        LocalDateTime lastEntry = null;
        for (TimeRecord record : sortedRecords) {
            EventType eventType = record.getEventType();
            if (eventType == EventType.START_DRIVE || eventType == EventType.ENTRY) {
                if (lastEntry == null) {
                    lastEntry = record.getTimestamp();
                }
            } else if ((eventType == EventType.END_DRIVE || eventType == EventType.EXIT) && lastEntry != null) {
                Duration segment = Duration.between(lastEntry, record.getTimestamp());
                currentContinuous = currentContinuous.plus(segment);
                if (currentContinuous.compareTo(maxContinuous) > 0) {
                    maxContinuous = currentContinuous;
                }
                lastEntry = null;
                currentContinuous = Duration.ZERO;
            }
        }
        LOGGER.log(Level.FINE, "Duração máxima de direção contínua: {0}", maxContinuous);
        return maxContinuous;
    }

    @Override
    public boolean hasRestPeriodAfterDriving(List<TimeRecord> timeRecords, Duration maxDrivingDuration) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return false;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getTimestamp))
                .collect(Collectors.toList());
        Duration currentDriving = Duration.ZERO;
        LocalDateTime lastEntry = null;
        boolean foundRestAfterMaxDriving = false;
        for (int i = 0; i < sortedRecords.size(); i++) {
            TimeRecord record = sortedRecords.get(i);
            EventType eventType = record.getEventType();
            if (eventType == EventType.START_DRIVE || eventType == EventType.ENTRY) {
                if (lastEntry == null) {
                    lastEntry = record.getTimestamp();
                }
            } else if ((eventType == EventType.END_DRIVE || eventType == EventType.EXIT) && lastEntry != null) {
                Duration segment = Duration.between(lastEntry, record.getTimestamp());
                currentDriving = currentDriving.plus(segment);
                if (currentDriving.compareTo(maxDrivingDuration) > 0) {
                    if (i + 1 < sortedRecords.size()) {
                        TimeRecord nextRecord = sortedRecords.get(i + 1);
                        if (nextRecord.getEventType() == EventType.REST || nextRecord.getEventType() == EventType.PAUSE) {
                            Duration restPeriod = Duration.between(record.getTimestamp(), nextRecord.getTimestamp());
                            if (restPeriod.toMinutes() >= 30) {
                                foundRestAfterMaxDriving = true;
                            }
                        }
                    }
                }
                lastEntry = null;
                currentDriving = Duration.ZERO;
            }
        }
        LOGGER.log(Level.FINE, "Verificação de período de descanso após direção: {0}", foundRestAfterMaxDriving);
        return foundRestAfterMaxDriving;
    }

    @Override
    public boolean hasInsufficientInterJourneyRest(LocalDateTime lastExit, LocalDateTime nextEntry, Duration minInterJourneyRest) {
        if (lastExit == null || nextEntry == null || minInterJourneyRest == null || minInterJourneyRest.isNegative()) {
            LOGGER.log(Level.WARNING, "Parâmetros inválidos para verificação de descanso interjornada.");
            return false;
        }
        if (nextEntry.isBefore(lastExit)) {
            LOGGER.log(Level.WARNING, "Próxima entrada ({0}) é anterior à última saída ({1}). Dados inconsistentes.",
                    new Object[]{nextEntry, lastExit});
            return false;
        }
        Duration restDuration = Duration.between(lastExit, nextEntry);
        boolean insufficient = restDuration.compareTo(minInterJourneyRest) < 0;
        if (insufficient) {
            LOGGER.log(Level.WARNING, "Descanso interjornada insuficiente: {0} (mínimo: {1})",
                    new Object[]{restDuration, minInterJourneyRest});
        } else {
            LOGGER.log(Level.FINE, "Descanso interjornada suficiente: {0} (mínimo: {1})",
                    new Object[]{restDuration, minInterJourneyRest});
        }
        return insufficient;
    }

    @Override
    public boolean hasInsufficientIntraJourneyRest(List<TimeRecord> timeRecords, Duration minIntraJourneyRest, Duration maxDrivingBeforeRest) {
        if (timeRecords == null || timeRecords.isEmpty() || minIntraJourneyRest == null ||
                minIntraJourneyRest.isNegative() || maxDrivingBeforeRest == null || maxDrivingBeforeRest.isNegative()) {
            LOGGER.log(Level.WARNING, "Parâmetros inválidos para verificação de descanso intrajornada.");
            return false;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getTimestamp))
                .collect(Collectors.toList());
        Duration currentDrivingSegment = Duration.ZERO;
        LocalDateTime lastEntry = null;
        LocalDateTime lastDrivingEnd = null;
        for (int i = 0; i < sortedRecords.size(); i++) {
            TimeRecord current = sortedRecords.get(i);
            EventType eventType = current.getEventType();

            if (eventType == EventType.START_DRIVE || eventType == EventType.ENTRY) {
                if (lastEntry == null) {
                    lastEntry = current.getTimestamp();
                }
            } else if ((eventType == EventType.END_DRIVE || eventType == EventType.EXIT) && lastEntry != null) {
                currentDrivingSegment = currentDrivingSegment.plus(Duration.between(lastEntry, current.getTimestamp()));
                lastDrivingEnd = current.getTimestamp();
                lastEntry = null;

                if (currentDrivingSegment.compareTo(maxDrivingBeforeRest) > 0) {
                    boolean foundSufficientRest = false;
                    for (int j = i + 1; j < sortedRecords.size(); j++) {
                        TimeRecord next = sortedRecords.get(j);
                        if (next.getEventType() == EventType.START_DRIVE || next.getEventType() == EventType.ENTRY) {
                            Duration restBetweenDriving = Duration.between(lastDrivingEnd, next.getTimestamp());
                            if (restBetweenDriving.compareTo(minIntraJourneyRest) >= 0) {
                                foundSufficientRest = true;
                                break;
                            } else {
                                LOGGER.log(Level.WARNING, "Descanso intrajornada insuficiente após segmento de direção excedido: {0} (mínimo: {1})",
                                        new Object[]{restBetweenDriving, minIntraJourneyRest});
                                return true;
                            }
                        }
                    }
                    if (!foundSufficientRest && i == sortedRecords.size() - 1) {
                        LOGGER.log(Level.WARNING, "Segmento de direção excedeu o limite e não houve descanso intrajornada suficiente até o final da jornada.");
                        return true;
                    }
                }
                currentDrivingSegment = Duration.ZERO;
            }
        }

        Duration totalIntraJourneyRest = calculateTotalRestDuration(timeRecords);
        if (totalIntraJourneyRest.compareTo(minIntraJourneyRest) < 0) {
            LOGGER.log(Level.WARNING, "Descanso intrajornada total insuficiente: {0} (mínimo: {1})",
                    new Object[]{totalIntraJourneyRest, minIntraJourneyRest});
            return true;
        }

        LOGGER.log(Level.FINE, "Descanso intrajornada suficiente.");
        return false;
    }

    @Override
    public Duration calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || start.isAfter(end)) {
            return Duration.ZERO;
        }
        return Duration.between(start, end);
    }
}