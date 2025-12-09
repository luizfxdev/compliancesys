package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate; // Import adicionado
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Import adicionado

import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public interface TimeRecordDAO {
    int create(TimeRecord timeRecord) throws SQLException;
    Optional<TimeRecord> findById(int id) throws SQLException;
    List<TimeRecord> findAll() throws SQLException;
    List<TimeRecord> findByDriverId(int driverId) throws SQLException;
    List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException;
    boolean update(TimeRecord timeRecord) throws SQLException;
    boolean delete(int id) throws SQLException;

    // ADICIONADO: Método para verificar duplicidade
    Optional<TimeRecord> findByDriverIdAndRecordTimeAndEventType(int driverId, LocalDateTime recordTime, EventType eventType) throws SQLException;
}
