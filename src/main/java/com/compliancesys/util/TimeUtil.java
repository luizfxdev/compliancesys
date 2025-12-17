package com.compliancesys.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.compliancesys.model.TimeRecord;

public interface TimeUtil {
    Duration calculateTotalWorkDuration(List<TimeRecord> timeRecords);

    Duration calculateTotalRestDuration(List<TimeRecord> timeRecords);

    boolean exceedsMaxContinuousDriving(List<TimeRecord> timeRecords, Duration maxContinuousDriving);

    boolean hasInsufficientInterJourneyRest(LocalDateTime lastExit, LocalDateTime nextEntry, Duration minInterJourneyRest);

    boolean hasInsufficientIntraJourneyRest(List<TimeRecord> timeRecords, Duration minIntraJourneyRest, Duration maxDrivingBeforeRest);

    Duration calculateDuration(LocalDateTime start, LocalDateTime end);

    Duration calculateMaxContinuousDriving(List<TimeRecord> timeRecords);

    boolean hasRestPeriodAfterDriving(List<TimeRecord> timeRecords, Duration maxDrivingDuration);
}
