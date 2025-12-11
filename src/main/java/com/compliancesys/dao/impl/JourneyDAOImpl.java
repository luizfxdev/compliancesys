package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;

public class JourneyDAOImpl implements JourneyDAO {

    private final Connection connection;

    public JourneyDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Journey journey) throws SQLException {
        String sql = "INSERT INTO journeys (driver_id, journey_date, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, journey.getDriverId());
            stmt.setDate(2, Date.valueOf(journey.getJourneyDate()));
            stmt.setInt(3, journey.getTotalDrivingTimeMinutes());
            stmt.setInt(4, journey.getTotalRestTimeMinutes());
            stmt.setString(5, journey.getComplianceStatus());
            stmt.setBoolean(6, journey.isDailyLimitExceeded());
            stmt.setTimestamp(7, Timestamp.valueOf(journey.getCreatedAt()));
            stmt.setTimestamp(8, Timestamp.valueOf(journey.getUpdatedAt()));

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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM journeys ORDER BY journey_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                journeys.add(mapResultSetToJourney(rs));
            }
        }
        return journeys;
    }

    @Override
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, journey_date = ?, total_driving_time_minutes = ?, total_rest_time_minutes = ?, compliance_status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journey.getDriverId());
            stmt.setDate(2, Date.valueOf(journey.getJourneyDate()));
            stmt.setInt(3, journey.getTotalDrivingTimeMinutes());
            stmt.setInt(4, journey.getTotalRestTimeMinutes());
            stmt.setString(5, journey.getComplianceStatus());
            stmt.setBoolean(6, journey.isDailyLimitExceeded());
            stmt.setTimestamp(7, Timestamp.valueOf(journey.getUpdatedAt()));
            stmt.setInt(8, journey.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Journey> findByDriverId(int driverId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE driver_id = ? ORDER BY journey_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM journeys WHERE driver_id = ? AND journey_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public List<Journey> findByVehicleId(int vehicleId) throws SQLException {
        // Nota: A tabela journeys no schema.sql não possui vehicle_id
        // Retornando lista vazia para evitar erro de compilação
        // Se vehicle_id for adicionado ao schema, este método deve ser atualizado
        return new ArrayList<>();
    }

    @Override
    public Optional<Journey> findByVehicleIdAndDate(int vehicleId, LocalDate journeyDate) throws SQLException {
        // Nota: A tabela journeys no schema.sql não possui vehicle_id
        // Retornando Optional vazio para evitar erro de compilação
        // Se vehicle_id for adicionado ao schema, este método deve ser atualizado
        return Optional.empty();
    }

    @Override
    public List<Journey> findByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE driver_id = ? AND journey_date BETWEEN ? AND ? ORDER BY journey_date ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public List<Journey> findByVehicleIdAndDateRange(int vehicleId, LocalDate startDate, LocalDate endDate) throws SQLException {
        // Nota: A tabela journeys no schema.sql não possui vehicle_id
        // Retornando lista vazia para evitar erro de compilação
        // Se vehicle_id for adicionado ao schema, este método deve ser atualizado
        return new ArrayList<>();
    }

    @Override
    public List<Journey> findByStatus(String status) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT * FROM journeys WHERE compliance_status = ? ORDER BY journey_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM journeys WHERE compliance_status = ? AND journey_date BETWEEN ? AND ? ORDER BY journey_date ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        journey.setJourneyDate(rs.getDate("journey_date").toLocalDate());
        journey.setTotalDrivingTimeMinutes(rs.getInt("total_driving_time_minutes"));
        journey.setTotalRestTimeMinutes(rs.getInt("total_rest_time_minutes"));
        journey.setComplianceStatus(rs.getString("compliance_status"));
        journey.setDailyLimitExceeded(rs.getBoolean("daily_limit_exceeded"));
        journey.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        journey.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return journey;
    }
}