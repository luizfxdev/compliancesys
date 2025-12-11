package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public class TimeRecordDAOImpl implements TimeRecordDAO {

    private final Connection connection;

    public TimeRecordDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(TimeRecord timeRecord) throws SQLException {
        String sql = "INSERT INTO time_records (driver_id, record_time, event_type, location, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setTimestamp(2, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(3, timeRecord.getEventType().name()); // Converte enum para String
            stmt.setString(4, timeRecord.getLocation());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao criar registro de ponto, nenhum ID gerado.");
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM time_records ORDER BY record_time ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        }
        return timeRecords;
    }

    @Override
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, record_time = ?, event_type = ?, location = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setTimestamp(2, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(3, timeRecord.getEventType().name()); // Converte enum para String
            stmt.setString(4, timeRecord.getLocation());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, timeRecord.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? ORDER BY record_time ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setTimestamp(2, Timestamp.valueOf(recordTime));
            stmt.setString(3, eventType);
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
        // Nota: A tabela time_records no schema.sql não possui journey_id
        // Esta é uma limitação do design atual do banco de dados
        // Para buscar TimeRecords de uma Journey, use findByDriverIdAndDate com os dados da Journey
        return new ArrayList<>();
    }

    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(rs.getInt("id"));
        timeRecord.setDriverId(rs.getInt("driver_id"));
        timeRecord.setRecordTime(rs.getTimestamp("record_time").toLocalDateTime());
        
        // Converte String do banco para enum EventType
        String eventTypeStr = rs.getString("event_type");
        timeRecord.setEventType(EventType.valueOf(eventTypeStr));
        
        timeRecord.setLocation(rs.getString("location"));
        timeRecord.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        timeRecord.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return timeRecord;
    }
}