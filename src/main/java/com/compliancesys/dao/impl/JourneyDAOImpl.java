package com.compliancesys.dao.impl;

<<<<<<< Updated upstream
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
=======
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.ComplianceStatus; // Importa o enum ComplianceStatus
import com.compliancesys.model.Journey;
import javax.sql.DataSource; // Importa DataSource
import java.sql.*;
import java.time.Duration; // Importa Duration
>>>>>>> Stashed changes
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
<<<<<<< Updated upstream

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;
=======
>>>>>>> Stashed changes

/**
 * Implementação do Data Access Object (DAO) para Journey.
 * Gerencia a persistência de objetos Journey no banco de dados.
 */
public class JourneyDAOImpl implements JourneyDAO {

<<<<<<< Updated upstream
    private final Connection connection;

    public JourneyDAOImpl(Connection connection) {
        this.connection = connection;
=======
    private final DataSource dataSource; // Adicionado DataSource

    // Construtor para injeção de dependência do DataSource
    public JourneyDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
>>>>>>> Stashed changes
    }

    @Override
    public int create(Journey journey) throws SQLException {
<<<<<<< Updated upstream
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
=======
        String sql = "INSERT INTO journeys (driver_id, vehicle_id, journey_date, start_time, end_time, start_location, end_location, total_driving_time, total_rest_time, total_break_time, status, daily_limit_exceeded, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
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
            stmt.setString(11, journey.getStatus().name()); // Converte enum para String
            stmt.setBoolean(12, journey.isDailyLimitExceeded());
            stmt.setObject(13, LocalDateTime.now());
            stmt.setObject(14, LocalDateTime.now());
>>>>>>> Stashed changes

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar jornada, nenhum ID gerado.");
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
<<<<<<< Updated upstream
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
=======
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        String sql = "SELECT * FROM journeys ORDER BY journey_date DESC";
        try (Statement stmt = connection.createStatement();
=======
        String sql = "SELECT * FROM journeys";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
             Statement stmt = conn.createStatement();
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        String sql = "SELECT * FROM journeys WHERE driver_id = ? ORDER BY journey_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
=======
        String sql = "SELECT * FROM journeys WHERE driver_id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
=======
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, vehicle_id = ?, journey_date = ?, start_time = ?, end_time = ?, start_location = ?, end_location = ?, total_driving_time = ?, total_rest_time = ?, total_break_time = ?, status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
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
            stmt.setString(11, journey.getStatus().name()); // Converte enum para String
            stmt.setBoolean(12, journey.isDailyLimitExceeded());
            stmt.setObject(13, LocalDateTime.now()); // Atualiza updated_at
            stmt.setInt(14, journey.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
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
                ComplianceStatus.valueOf(rs.getString("status")), // Converte String para enum
                rs.getBoolean("daily_limit_exceeded"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
>>>>>>> Stashed changes
    }
}
