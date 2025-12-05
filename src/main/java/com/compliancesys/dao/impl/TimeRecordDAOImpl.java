package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Importa o enum EventType
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public class TimeRecordDAOImpl implements TimeRecordDAO {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordDAOImpl.class.getName());

    @Override
    public int create(TimeRecord timeRecord) throws SQLException { // CORRIGIDO: Retorna int
        String sql = "INSERT INTO time_records (driver_id, vehicle_id, record_time, event_type, location, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getVehicleId());
            stmt.setObject(3, timeRecord.getRecordTime());
            stmt.setString(4, timeRecord.getEventType().name());
            stmt.setString(5, timeRecord.getLocation());
            stmt.setObject(6, timeRecord.getCreatedAt());
            stmt.setObject(7, timeRecord.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar registro de ponto, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar registro de ponto, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, record_time, event_type, location, created_at, updated_at FROM time_records WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TimeRecord> findAll() throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, record_time, event_type, location, created_at, updated_at FROM time_records";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, record_time, event_type, location, created_at, updated_at FROM time_records WHERE driver_id = ? AND DATE(record_time) = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, date); // LocalDate
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, vehicle_id = ?, record_time = ?, event_type = ?, location = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getVehicleId());
            stmt.setObject(3, timeRecord.getRecordTime());
            stmt.setString(4, timeRecord.getEventType().name());
            stmt.setString(5, timeRecord.getLocation());
            stmt.setObject(6, timeRecord.getUpdatedAt());
            stmt.setInt(7, timeRecord.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
        return new TimeRecord(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("vehicle_id"),
                rs.getObject("record_time", LocalDateTime.class),
                EventType.valueOf(rs.getString("event_type")),
                rs.getString("location"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
