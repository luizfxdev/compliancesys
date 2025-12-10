package com.compliancesys.dao.impl;

import java.sql.Connection;
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

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;

public class JourneyDAOImpl implements JourneyDAO {

    private static final Logger LOGGER = Logger.getLogger(JourneyDAOImpl.class.getName());
    private Connection connection; // Adicionado para permitir injeção de conexão para testes

    // Construtor para injeção de dependência (usado em testes)
    public JourneyDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // Construtor padrão (usado em produção)
    public JourneyDAOImpl() {
        // A conexão será obtida via DatabaseConfig.getInstance().getConnection() nos métodos
    }

    // Método auxiliar para obter a conexão, priorizando a injetada
    private Connection getConnection() throws SQLException {
        return (this.connection != null) ? this.connection : DatabaseConfig.getInstance().getConnection();
    }

    @Override
    public int create(Journey journey) throws SQLException {
        String sql = "INSERT INTO journeys (driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setObject(3, journey.getJourneyDate());
            stmt.setObject(4, journey.getStartTime());
            stmt.setObject(5, journey.getEndTime());
            stmt.setString(6, journey.getStartLocation());
            stmt.setString(7, journey.getEndLocation());
            stmt.setDouble(8, journey.getTotalDistance());
            stmt.setDouble(9, journey.getTotalDuration());
            stmt.setDouble(10, journey.getDrivingDuration());
            stmt.setDouble(11, journey.getBreakDuration());
            stmt.setDouble(12, journey.getRestDuration());
            stmt.setDouble(13, journey.getMealDuration());
            stmt.setString(14, journey.getStatus());
            stmt.setBoolean(15, journey.isDailyLimitExceeded());
            stmt.setObject(16, LocalDateTime.now()); // created_at
            stmt.setObject(17, LocalDateTime.now()); // updated_at

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating journey failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating journey failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Optional<Journey> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE id = ?";
        try (Connection conn = getConnection();
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
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                journeys.add(mapResultSetToJourney(rs));
            }
        }
        return journeys;
    }

    @Override
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, vehicle_id = ?, journey_date = ?, start_time = ?, end_time = ?, start_location = ?, end_location = ?, total_distance = ?, total_duration = ?, driving_duration = ?, break_duration = ?, rest_duration = ?, meal_duration = ?, status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setObject(3, journey.getJourneyDate());
            stmt.setObject(4, journey.getStartTime());
            stmt.setObject(5, journey.getEndTime());
            stmt.setString(6, journey.getStartLocation());
            stmt.setString(7, journey.getEndLocation());
            stmt.setDouble(8, journey.getTotalDistance());
            stmt.setDouble(9, journey.getTotalDuration());
            stmt.setDouble(10, journey.getDrivingDuration());
            stmt.setDouble(11, journey.getBreakDuration());
            stmt.setDouble(12, journey.getRestDuration());
            stmt.setDouble(13, journey.getMealDuration());
            stmt.setString(14, journey.getStatus());
            stmt.setBoolean(15, journey.isDailyLimitExceeded());
            stmt.setObject(16, LocalDateTime.now()); // updated_at
            stmt.setInt(17, journey.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Journey> findByDriverId(int driverId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ?";
        try (Connection conn = getConnection();
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
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ? AND journey_date = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, journeyDate); // Corrigido: stmt.setObject
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJourney(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Journey> findByVehicleId(int vehicleId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
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
    public Optional<Journey> findByVehicleIdAndDate(int vehicleId, LocalDate journeyDate) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE vehicle_id = ? AND journey_date = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
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
    public List<Journey> findByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ? AND journey_date BETWEEN ? AND ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        }
        return journeys;
    }

    @Override
    public List<Journey> findByVehicleIdAndDateRange(int vehicleId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE vehicle_id = ? AND journey_date BETWEEN ? AND ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
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
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE status = ?";
        try (Connection conn = getConnection();
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
        String sql = "SELECT id, driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_distance, total_duration, driving_duration, break_duration, rest_duration, meal_duration, status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE status = ? AND journey_date BETWEEN ? AND ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
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
        journey.setJourneyDate(rs.getObject("journey_date", LocalDate.class));
        journey.setStartTime(rs.getObject("start_time", LocalDateTime.class));
        Timestamp endTimeStamp = rs.getTimestamp("end_time");
        journey.setEndTime(endTimeStamp != null ? endTimeStamp.toLocalDateTime() : null);
        journey.setStartLocation(rs.getString("start_location"));
        journey.setEndLocation(rs.getString("end_location"));
        journey.setTotalDistance(rs.getDouble("total_distance"));
        journey.setTotalDuration(rs.getDouble("total_duration"));
        journey.setDrivingDuration(rs.getDouble("driving_duration"));
        journey.setBreakDuration(rs.getDouble("break_duration"));
        journey.setRestDuration(rs.getDouble("rest_duration"));
        journey.setMealDuration(rs.getDouble("meal_duration"));
        journey.setStatus(rs.getString("status"));
        journey.setDailyLimitExceeded(rs.getBoolean("daily_limit_exceeded"));
        journey.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        journey.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return journey;
    }
}
