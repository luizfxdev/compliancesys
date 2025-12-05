package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;

public class JourneyDAOImpl implements JourneyDAO {

    private static final Logger LOGGER = Logger.getLogger(JourneyDAOImpl.class.getName());

    @Override
    public int create(Journey journey) throws SQLException {
        String sql = "INSERT INTO journeys (driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_driving_time, total_rest_time, total_break_time, status, daily_limit_exceeded, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setObject(3, journey.getJourneyDate());
            stmt.setObject(4, journey.getStartTime());
            stmt.setObject(5, journey.getEndTime());
            stmt.setString(6, journey.getStartLocation());
            stmt.setString(7, journey.getEndLocation());
            stmt.setObject(8, journey.getTotalDrivingTime());
            stmt.setObject(9, journey.getTotalRestTime());
            stmt.setObject(10, journey.getTotalBreakTime());
            stmt.setString(11, journey.getStatus().name());
            stmt.setBoolean(12, journey.isDailyLimitExceeded());
            stmt.setObject(13, journey.getCreatedAt());
            stmt.setObject(14, journey.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar jornada, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar jornada, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<Journey> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_driving_time, total_rest_time, total_break_time, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJourney(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Journey> findAll() throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_driving_time, total_rest_time, total_break_time, status, daily_limit_exceeded, created_at, updated_at FROM journeys";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                journeys.add(mapResultSetToJourney(rs));
            }
        }
        return journeys;
    }

    @Override
    public List<Journey> findByDriverId(int driverId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_driving_time, total_rest_time, total_break_time, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    @Override
    public Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_driving_time, total_rest_time, total_break_time, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ? AND journey_date = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, journeyDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJourney(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, vehicle_id = ?, journey_date = ?, start_time = ?, end_time = ?, start_location = ?, end_location = ?, total_driving_time = ?, total_rest_time = ?, total_break_time = ?, status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setObject(3, journey.getJourneyDate());
            stmt.setObject(4, journey.getStartTime());
            stmt.setObject(5, journey.getEndTime());
            stmt.setString(6, journey.getStartLocation());
            stmt.setString(7, journey.getEndLocation());
            stmt.setObject(8, journey.getTotalDrivingTime());
            stmt.setObject(9, journey.getTotalRestTime());
            stmt.setObject(10, journey.getTotalBreakTime());
            stmt.setString(11, journey.getStatus().name());
            stmt.setBoolean(12, journey.isDailyLimitExceeded());
            stmt.setObject(13, journey.getUpdatedAt());
            stmt.setInt(14, journey.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Journey mapResultSetToJourney(ResultSet rs) throws SQLException {
        return new Journey(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("vehicle_id"),
                rs.getObject("journey_date", LocalDate.class),
                rs.getObject("start_time", LocalDateTime.class),
                rs.getObject("end_time", LocalDateTime.class),
                rs.getString("start_location"),
                rs.getString("end_location"),
                rs.getObject("total_driving_time", Duration.class),
                rs.getObject("total_rest_time", Duration.class),
                rs.getObject("total_break_time", Duration.class),
                ComplianceStatus.valueOf(rs.getString("status")),
                rs.getBoolean("daily_limit_exceeded"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}