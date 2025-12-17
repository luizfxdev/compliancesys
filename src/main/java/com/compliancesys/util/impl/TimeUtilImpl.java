package com.compliancesys.util.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.TimeUtil;

public class TimeUtilImpl implements TimeUtil {
    private static final Logger LOGGER = Logger.getLogger(TimeUtilImpl.class.getName());

    @Override
    public Duration calculateTotalWorkDuration(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return Duration.ZERO;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime))
                .collect(Collectors.toList());

        Duration totalWork = Duration.ZERO;
        LocalDateTime startWork = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_WORK || record.getEventType() == EventType.START_DRIVE) {
                if (startWork == null) {
                    startWork = record.getRecordTime();
                }
            } else if (record.getEventType() == EventType.END_WORK || record.getEventType() == EventType.END_DRIVE) {
                if (startWork != null) {
                    totalWork = totalWork.plus(Duration.between(startWork, record.getRecordTime()));
                    startWork = null;
                }
            }
        }
        if (startWork != null && !sortedRecords.isEmpty()) {
            totalWork = totalWork.plus(Duration.between(startWork, sortedRecords.get(sortedRecords.size() - 1).getRecordTime()));
        }
        return totalWork;
    }

    @Override
    public Duration calculateTotalRestDuration(List<TimeRecord> timeRecords) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return Duration.ZERO;
        }
        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime))
                .collect(Collectors.toList());

        Duration totalRest = Duration.ZERO;
        LocalDateTime startRest = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_REST) {
                if (startRest == null) {
                    startRest = record.getRecordTime();
                }
            } else if (record.getEventType() == EventType.END_REST) {
                if (startRest != null) {
                    totalRest = totalRest.plus(Duration.between(startRest, record.getRecordTime()));
                    startRest = null;
                }
            }
        }
        if (startRest != null && !sortedRecords.isEmpty()) {
            totalRest = totalRest.plus(Duration.between(startRest, sortedRecords.get(sortedRecords.size() - 1).getRecordTime()));
        }
        return totalRest;
    }

    @Override
    public boolean exceedsMaxContinuousDriving(List<TimeRecord> timeRecords, Duration maxContinuousDriving) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return false;
        }

        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime))
                .collect(Collectors.toList());

        Duration currentDrivingSegment = Duration.ZERO;
        LocalDateTime lastEntry = null;
        EventType lastEventType = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_DRIVE) {
                lastEntry = record.getRecordTime();
                lastEventType = EventType.START_DRIVE;
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastEntry != null && lastEventType == EventType.START_DRIVE) {
                    currentDrivingSegment = currentDrivingSegment.plus(Duration.between(lastEntry, record.getRecordTime()));
                    if (currentDrivingSegment.compareTo(maxContinuousDriving) > 0) {
                        return true;
                    }
                    lastEntry = null;
                    lastEventType = null;
                }
            } else if (record.getEventType() == EventType.START_REST || record.getEventType() == EventType.END_REST || 
                       record.getEventType() == EventType.START_WORK || record.getEventType() == EventType.END_WORK) {
                currentDrivingSegment = Duration.ZERO;
                lastEntry = null;
                lastEventType = null;
            }
        }
        if (lastEntry != null && lastEventType == EventType.START_DRIVE && !sortedRecords.isEmpty()) {
            currentDrivingSegment = currentDrivingSegment.plus(Duration.between(lastEntry, sortedRecords.get(sortedRecords.size() - 1).getRecordTime()));
            if (currentDrivingSegment.compareTo(maxContinuousDriving) > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasInsufficientInterJourneyRest(LocalDateTime lastExit, LocalDateTime nextEntry, Duration minInterJourneyRest) {
        if (lastExit == null || nextEntry == null) {
            return false;
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
                .sorted(Comparator.comparing(TimeRecord::getRecordTime))
                .collect(Collectors.toList());

        Duration drivingSinceLastRest = Duration.ZERO;
        LocalDateTime lastDrivingStart = null;
        LocalDateTime lastRestEnd = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_DRIVE) {
                lastDrivingStart = record.getRecordTime();
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastDrivingStart != null) {
                    drivingSinceLastRest = drivingSinceLastRest.plus(Duration.between(lastDrivingStart, record.getRecordTime()));
                    lastDrivingStart = null;
                }
            } else if (record.getEventType() == EventType.START_REST) {
                drivingSinceLastRest = Duration.ZERO;
                lastRestEnd = null;
            } else if (record.getEventType() == EventType.END_REST) {
                lastRestEnd = record.getRecordTime();
            }

            if (drivingSinceLastRest.compareTo(maxDrivingBeforeRest) > 0) {
                if (lastRestEnd == null || Duration.between(lastRestEnd, record.getRecordTime()).compareTo(minIntraJourneyRest) < 0) {
                    return true;
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
                .sorted(Comparator.comparing(TimeRecord::getRecordTime))
                .collect(Collectors.toList());

        Duration maxContinuous = Duration.ZERO;
        Duration currentContinuous = Duration.ZERO;
        LocalDateTime lastDrivingStart = null;
        LocalDateTime lastEventRecordTime = null;

        for (TimeRecord record : sortedRecords) {
            if (lastEventRecordTime != null) {
                if (record.getRecordTime().isAfter(lastEventRecordTime) && 
                    (record.getEventType() == EventType.START_REST || record.getEventType() == EventType.END_REST ||
                     record.getEventType() == EventType.START_WORK || record.getEventType() == EventType.END_WORK)) {
                    currentContinuous = Duration.ZERO;
                    lastDrivingStart = null;
                }
            }

            if (record.getEventType() == EventType.START_DRIVE) {
                lastDrivingStart = record.getRecordTime();
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastDrivingStart != null) {
                    currentContinuous = currentContinuous.plus(Duration.between(lastDrivingStart, record.getRecordTime()));
                    if (currentContinuous.compareTo(maxContinuous) > 0) {
                        maxContinuous = currentContinuous;
                    }
                    currentContinuous = Duration.ZERO;
                    lastDrivingStart = null;
                }
            }
            lastEventRecordTime = record.getRecordTime();
        }

        if (lastDrivingStart != null && !sortedRecords.isEmpty()) {
            currentContinuous = currentContinuous.plus(Duration.between(lastDrivingStart, sortedRecords.get(sortedRecords.size() - 1).getRecordTime()));
            if (currentContinuous.compareTo(maxContinuous) > 0) {
                maxContinuous = currentContinuous;
            }
        }

        return maxContinuous;
    }

    @Override
    public boolean hasRestPeriodAfterDriving(List<TimeRecord> timeRecords, Duration maxDrivingDuration) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return true;
        }

        List<TimeRecord> sortedRecords = timeRecords.stream()
                .sorted(Comparator.comparing(TimeRecord::getRecordTime))
                .collect(Collectors.toList());

        Duration currentDriving = Duration.ZERO;
        LocalDateTime lastDrivingStart = null;
        LocalDateTime lastDrivingEnd = null;
        LocalDateTime lastRestEnd = null;

        for (TimeRecord record : sortedRecords) {
            if (record.getEventType() == EventType.START_DRIVE) {
                lastDrivingStart = record.getRecordTime();
            } else if (record.getEventType() == EventType.END_DRIVE) {
                if (lastDrivingStart != null) {
                    currentDriving = currentDriving.plus(Duration.between(lastDrivingStart, record.getRecordTime()));
                    lastDrivingEnd = record.getRecordTime();
                    lastDrivingStart = null;
                }
            } else if (record.getEventType() == EventType.END_REST) {
                lastRestEnd = record.getRecordTime();
                currentDriving = Duration.ZERO;
            }

            if (currentDriving.compareTo(maxDrivingDuration) > 0) {
                if (lastDrivingEnd != null && (lastRestEnd == null || lastRestEnd.isBefore(lastDrivingEnd))) {
                    return false;
                }
            }
        }
        return true;
    }
}
