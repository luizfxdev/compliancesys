package com.compliancesys.dao.impl;

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeRecordDAOImpl implements TimeRecordDAO {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordDAOImpl.class.getName());

    @Override
    public int create(TimeRecord timeRecord) throws SQLException {
        String sql = "INSERT INTO time_records (driver_id, company_id, vehicle_id, record_time, event_type, location, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getCompanyId());
            stmt.setInt(3, timeRecord.getVehicleId());
            stmt.setTimestamp(4, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(5, timeRecord.getEventType().name()); // Salva o nome do enum
            stmt.setString(6, timeRecord.getLocation());
            stmt.setString(7, timeRecord.getNotes());
            stmt.setTimestamp(8, Timestamp.valueOf(timeRecord.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(timeRecord.getUpdatedAt()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar registro de ponto, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar registro de ponto, nenhum ID gerado.");
                }
            }
        }
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
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
        String sql = "SELECT * FROM time_records";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        }
        return timeRecords;
    }

    @Override
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, company_id = ?, vehicle_id = ?, record_time = ?, event_type = ?, location = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getCompanyId());
            stmt.setInt(3, timeRecord.getVehicleId());
            stmt.setTimestamp(4, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(5, timeRecord.getEventType().name());
            stmt.setString(6, timeRecord.getLocation());
            stmt.setString(7, timeRecord.getNotes());
            stmt.setTimestamp(8, Timestamp.valueOf(timeRecord.getUpdatedAt()));
            stmt.setInt(9, timeRecord.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? ORDER BY record_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? AND DATE(record_time) = ? ORDER BY record_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
    public Optional<TimeRecord> findByDriverIdAndRecordTimeAndEventType(int driverId, LocalDateTime recordTime, String eventType) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE driver_id = ? AND record_time = ? AND event_type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setTimestamp(2, Timestamp.valueOf(recordTime));
            stmt.setString(3, eventType); // eventType já é String aqui
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TimeRecord> findByJourneyId(int journeyId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT tr.* FROM time_records tr JOIN journeys j ON tr.driver_id = j.driver_id AND DATE(tr.record_time) = j.journey_date WHERE j.id = ? ORDER BY tr.record_time ASC";
        // Esta query é uma suposição de como você associa TimeRecords a Journeys.
        // Se a sua tabela time_records tiver uma coluna journey_id, a query seria mais simples:
        // String sql = "SELECT * FROM time_records WHERE journey_id = ? ORDER BY record_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(rs.getInt("id"));
        timeRecord.setDriverId(rs.getInt("driver_id"));
        timeRecord.setCompanyId(rs.getInt("company_id"));
        timeRecord.setVehicleId(rs.getInt("vehicle_id"));
        timeRecord.setRecordTime(rs.getTimestamp("record_time").toLocalDateTime());
        timeRecord.setEventType(EventType.valueOf(rs.getString("event_type")));
        timeRecord.setLocation(rs.getString("location"));
        timeRecord.setNotes(rs.getString("notes"));
        timeRecord.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        timeRecord.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return timeRecord;
    }
}
