package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.TimeRecord;

public interface TimeRecordDAO {
    int create(TimeRecord timeRecord) throws SQLException;
    Optional<TimeRecord> findById(int id) throws SQLException;
    List<TimeRecord> findAll() throws SQLException;
    boolean update(TimeRecord timeRecord) throws SQLException;
    boolean delete(int id) throws SQLException;
    List<TimeRecord> findByDriverId(int driverId) throws SQLException;
    List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException;
    Optional<TimeRecord> findByDriverIdAndRecordTimeAndEventType(int driverId, LocalDateTime recordTime, String eventType) throws SQLException;

    /**
     * Busca registros de ponto por ID de jornada.
     * @param journeyId O ID da jornada.
     * @return Uma lista de registros de ponto associados à jornada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findByJourneyId(int journeyId) throws SQLException; // ADICIONADO
}
