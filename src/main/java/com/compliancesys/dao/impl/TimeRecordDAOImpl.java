package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.ConnectionFactory;

public class TimeRecordDAOImpl implements TimeRecordDAO {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordDAOImpl.class.getName());
    private final ConnectionFactory connectionFactory;

    public TimeRecordDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public int create(TimeRecord timeRecord) throws SQLException {
        String sql = "INSERT INTO time_records (driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getJourneyId());
            stmt.setTimestamp(3, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(4, timeRecord.getEventType().name());
            stmt.setString(5, timeRecord.getLocation());
            if (timeRecord.getLatitude() != null) {
                stmt.setDouble(6, timeRecord.getLatitude());
            } else {
                stmt.setNull(6, Types.DOUBLE);
            }
            if (timeRecord.getLongitude() != null) {
                stmt.setDouble(7, timeRecord.getLongitude());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar registro de tempo, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar registro de tempo, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
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
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        }
        return timeRecords;
    }

    @Override
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, journey_id = ?, record_time = ?, event_type = ?, location = ?, latitude = ?, longitude = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getJourneyId());
            stmt.setTimestamp(3, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(4, timeRecord.getEventType().name());
            stmt.setString(5, timeRecord.getLocation());
            if (timeRecord.getLatitude() != null) {
                stmt.setDouble(6, timeRecord.getLatitude());
            } else {
                stmt.setNull(6, Types.DOUBLE);
            }
            if (timeRecord.getLongitude() != null) {
                stmt.setDouble(7, timeRecord.getLongitude());
            } else {
                stmt.setNull(7, Types.DOUBLE);
            }
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(9, timeRecord.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<TimeRecord> findByJourneyId(int journeyId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE journey_id = ? ORDER BY record_time ASC";
        try (Connection conn = connectionFactory.getConnection();
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

    @Override
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? ORDER BY record_time ASC";
        try (Connection conn = connectionFactory.getConnection();
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
    public List<TimeRecord> findByEventType(EventType eventType) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE event_type = ? ORDER BY record_time ASC";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByRecordTimeRange(LocalDateTime start, LocalDateTime end) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE record_time BETWEEN ? AND ? ORDER BY record_time ASC";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
    public Optional<TimeRecord> findLatestByDriverIdAndJourneyId(int driverId, int journeyId) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE driver_id = ? AND journey_id = ? ORDER BY record_time DESC LIMIT 1";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return Optional.empty();
    }

    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(rs.getInt("id"));
        timeRecord.setDriverId(rs.getInt("driver_id"));
        timeRecord.setJourneyId(rs.getInt("journey_id"));
        timeRecord.setRecordTime(rs.getTimestamp("record_time").toLocalDateTime());
        timeRecord.setEventType(EventType.valueOf(rs.getString("event_type")));
        timeRecord.setLocation(rs.getString("location"));
        timeRecord.setLatitude(rs.getObject("latitude", Double.class));
        timeRecord.setLongitude(rs.getObject("longitude", Double.class));
        timeRecord.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        timeRecord.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return timeRecord;
    }
}
