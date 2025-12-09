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
import java.util.logging.Logger;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;
import com.compliancesys.util.DatabaseConnection;

public class JourneyDAOImpl implements JourneyDAO {

    private static final Logger LOGGER = Logger.getLogger(JourneyDAOImpl.class.getName());

    @Override
    public int create(Journey journey) throws SQLException {
        String sql = "INSERT INTO journeys (driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, " +
                     "total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setDate(3, Date.valueOf(journey.getJourneyDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(journey.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(journey.getEndTime()));
            stmt.setString(6, journey.getStartLocation());
            stmt.setString(7, journey.getEndLocation());
            stmt.setLong(8, journey.getTotalDuration());
            stmt.setLong(9, journey.getDrivingDuration());
            stmt.setLong(10, journey.getBreakDuration());
            stmt.setLong(11, journey.getRestDuration());
            stmt.setLong(12, journey.getMealDuration());
            stmt.setString(13, journey.getStatus()); // NOVO
            stmt.setBoolean(14, journey.isDailyLimitExceeded()); // NOVO
            stmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar jornada, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar jornada, nenhum ID gerado.");
                }
            }
        }
    }

    @Override
    public Optional<Journey> findById(int id) throws SQLException {
        String sql = "SELECT * FROM journeys WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
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
        String sql = "SELECT * FROM journeys";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                journeys.add(mapResultSetToJourney(rs));
            }
        }
        return journeys;
    }

    @Override
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, vehicle_id = ?, journey_date = ?, start_time = ?, end_time = ?, " +
                     "start_location = ?, end_location = ?, total_duration = ?, driving_duration = ?, break_duration = ?, " +
                     "rest_duration = ?, meal_duration = ?, status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setDate(3, Date.valueOf(journey.getJourneyDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(journey.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(journey.getEndTime()));
            stmt.setString(6, journey.getStartLocation());
            stmt.setString(7, journey.getEndLocation());
            stmt.setLong(8, journey.getTotalDuration());
            stmt.setLong(9, journey.getDrivingDuration());
            stmt.setLong(10, journey.getBreakDuration());
            stmt.setLong(11, journey.getRestDuration());
            stmt.setLong(12, journey.getMealDuration());
            stmt.setString(13, journey.getStatus()); // NOVO
            stmt.setBoolean(14, journey.isDailyLimitExceeded()); // NOVO
            stmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(16, journey.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException {
        String sql = "SELECT * FROM journeys WHERE driver_id = ? AND journey_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setDate(2, Date.valueOf(journeyDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJourney(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Journey> findByDriverId(int driverId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE driver_id = ? ORDER BY journey_date DESC, start_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
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
    public List<Journey> findByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE driver_id = ? AND journey_date BETWEEN ? AND ? ORDER BY journey_date ASC, start_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    @Override
    public List<Journey> findByVehicleId(int vehicleId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE vehicle_id = ? ORDER BY journey_date DESC, start_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    @Override
    public List<Journey> findByVehicleIdAndDate(int vehicleId, LocalDate journeyDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE vehicle_id = ? AND journey_date = ? ORDER BY start_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setDate(2, Date.valueOf(journeyDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    @Override
    public List<Journey> findByStatus(String status) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE status = ? ORDER BY journey_date DESC, start_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    @Override
    public List<Journey> findByStatusAndDateRange(String status, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE status = ? AND journey_date BETWEEN ? AND ? ORDER BY journey_date ASC, start_time ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    private Journey mapResultSetToJourney(ResultSet rs) throws SQLException {
        Journey journey = new Journey();
        journey.setId(rs.getInt("id"));
        journey.setDriverId(rs.getInt("driver_id"));
        journey.setVehicleId(rs.getInt("vehicle_id"));
        journey.setJourneyDate(rs.getDate("journey_date").toLocalDate());
        journey.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        journey.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        journey.setStartLocation(rs.getString("start_location"));
        journey.setEndLocation(rs.getString("end_location"));
        journey.setTotalDuration(rs.getLong("total_duration"));
        journey.setDrivingDuration(rs.getLong("driving_duration"));
        journey.setBreakDuration(rs.getLong("break_duration"));
        journey.setRestDuration(rs.getLong("rest_duration"));
        journey.setMealDuration(rs.getLong("meal_duration"));
        journey.setStatus(rs.getString("status")); // NOVO
        journey.setDailyLimitExceeded(rs.getBoolean("daily_limit_exceeded")); // NOVO
        journey.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        journey.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return journey;
    }
}
