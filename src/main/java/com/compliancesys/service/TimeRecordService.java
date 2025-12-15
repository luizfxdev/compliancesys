// src/main/java/com/compliancesys/service/TimeRecordService.java
package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List; // Certifique-se de que este import existe
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public interface TimeRecordService {
    TimeRecord registerTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException;
    Optional<TimeRecord> getTimeRecordById(int id) throws BusinessException, SQLException;
    TimeRecord updateTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException;
    boolean deleteTimeRecord(int id) throws BusinessException, SQLException;
    List<TimeRecord> getAllTimeRecords() throws BusinessException, SQLException;

    // Métodos adicionados/corrigidos anteriormente
    List<TimeRecord> getTimeRecordsByJourneyId(int journeyId) throws BusinessException, SQLException;
    List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException, SQLException; // Renomeado de getTimeRecordsByDriver
    List<TimeRecord> getTimeRecordsByEventType(EventType eventType) throws BusinessException, SQLException;
    List<TimeRecord> getTimeRecordsByDriverAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException;
    List<TimeRecord> getTimeRecordsByJourneyIdOrderedByTimestamp(int journeyId) throws BusinessException, SQLException;

    // Este é o método que precisa ser consistente:
    Optional<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException, SQLException; // CONFIRMADO: Retorna Optional<TimeRecord>
}
