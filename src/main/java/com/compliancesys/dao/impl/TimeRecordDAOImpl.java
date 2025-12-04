package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.TimeRecord.EventType;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface TimeRecordDAO para operações de persistência da entidade TimeRecord.
 * Interage com o banco de dados PostgreSQL.
 */
public class TimeRecordDAOImpl implements TimeRecordDAO {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public TimeRecordDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(TimeRecord timeRecord) throws SQLException {
        String sql = "INSERT INTO time_records (driver_id, record_time, event_type, location, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setObject(2, timeRecord.getRecordTime()); // LocalDateTime
            stmt.setString(3, timeRecord.getEventType().name()); // Enum como String
            stmt.setString(4, timeRecord.getLocation());
            stmt.setObject(5, timeRecord.getCreatedAt());
            stmt.setObject(6, timeRecord.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar registro de ponto: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, record_time, event_type, location, created_at, updated_at FROM time_records WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTimeRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar registro de ponto por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<TimeRecord> findAll() throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, record_time, event_type, location, created_at, updated_at FROM time_records ORDER BY record_time ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os registros de ponto: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, record_time, event_type, location, created_at, updated_at FROM time_records WHERE driver_id = ? ORDER BY record_time ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar registros de ponto por ID do motorista: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        // Busca registros dentro do período de um dia específico para o driver
        String sql = "SELECT id, driver_id, record_time, event_type, location, created_at, updated_at FROM time_records " +
                     "WHERE driver_id = ? AND record_time >= ? AND record_time < ? ORDER BY record_time ASC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            stmt.setObject(2, date.atStartOfDay()); // Início do dia
            stmt.setObject(3, date.plusDays(1).atStartOfDay()); // Início do próximo dia

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar registros de ponto por ID do motorista e data: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, record_time = ?, event_type = ?, location = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setObject(2, timeRecord.getRecordTime());
            stmt.setString(3, timeRecord.getEventType().name());
            stmt.setString(4, timeRecord.getLocation());
            stmt.setObject(5, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(6, timeRecord.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar registro de ponto: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar registro de ponto: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto TimeRecord.
     * @param rs ResultSet contendo os dados do registro de ponto.
     * @return Objeto TimeRecord.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(rs.getInt("id"));
        timeRecord.setDriverId(rs.getInt("driver_id"));
        timeRecord.setRecordTime(rs.getObject("record_time", LocalDateTime.class)); // LocalDateTime
        timeRecord.setEventType(EventType.valueOf(rs.getString("event_type"))); // String para Enum
        timeRecord.setLocation(rs.getString("location"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        timeRecord.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        timeRecord.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return timeRecord;
    }
}
