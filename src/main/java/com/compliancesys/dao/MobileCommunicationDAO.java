package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.enums.EventType;

public interface MobileCommunicationDAO {
    int create(MobileCommunication communication) throws SQLException;
    Optional<MobileCommunication> findById(int id) throws SQLException;
    List<MobileCommunication> findAll() throws SQLException;
    boolean update(MobileCommunication communication) throws SQLException;
    boolean delete(int id) throws SQLException;

    List<MobileCommunication> findByJourneyId(int journeyId) throws SQLException;
    List<MobileCommunication> findByDriverId(int driverId) throws SQLException;
    List<MobileCommunication> findByEventType(EventType eventType) throws SQLException;
    List<MobileCommunication> findByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException;
}
