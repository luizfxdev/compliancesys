package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public interface TimeRecordService {
    TimeRecord createTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException;
    TimeRecord updateTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException;
    boolean deleteTimeRecord(int id) throws BusinessException, SQLException;
    Optional<TimeRecord> getTimeRecordById(int id) throws SQLException;
    List<TimeRecord> getAllTimeRecords() throws SQLException;
    List<TimeRecord> getTimeRecordsByJourneyId(int journeyId) throws BusinessException, SQLException;
    List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException, SQLException;
    List<TimeRecord> getTimeRecordsByEventType(EventType eventType) throws SQLException;
    List<TimeRecord> getTimeRecordsByRecordTimeRange(LocalDateTime start, LocalDateTime end) throws SQLException;
    Optional<TimeRecord> getLatestTimeRecordByDriverAndJourney(int driverId, int journeyId) throws BusinessException, SQLException;
}
