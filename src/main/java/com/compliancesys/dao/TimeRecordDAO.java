package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public interface TimeRecordDAO {
    int create(TimeRecord timeRecord) throws SQLException;
    Optional<TimeRecord> findById(int id) throws SQLException;
    List<TimeRecord> findAll() throws SQLException;
    boolean update(TimeRecord timeRecord) throws SQLException;
    boolean delete(int id) throws SQLException;

    List<TimeRecord> findByJourneyId(int journeyId) throws SQLException;
    List<TimeRecord> findByDriverId(int driverId) throws SQLException;
    List<TimeRecord> findByEventType(EventType eventType) throws SQLException;
    List<TimeRecord> findByRecordTimeRange(LocalDateTime start, LocalDateTime end) throws SQLException;
    Optional<TimeRecord> findLatestByDriverIdAndJourneyId(int driverId, int journeyId) throws SQLException;
}
