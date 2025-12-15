// src/main/java/com/compliancesys/dao/TimeRecordDAO.java (Interface)
package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate; // Importar o enum
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

    // Métodos adicionados/confirmados com base nas necessidades do sistema
    List<TimeRecord> findByJourneyId(int journeyId) throws SQLException;
    List<TimeRecord> findByDriverId(int driverId) throws SQLException;
    List<TimeRecord> findByEventType(EventType eventType) throws SQLException; // Necessário pelo log
    List<TimeRecord> findByDriverAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException; // Necessário pelo log
    List<TimeRecord> findByJourneyIdOrderedByTimestamp(int journeyId) throws SQLException; // Necessário pelo log
    List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException; // Adicionado para consistência
}
