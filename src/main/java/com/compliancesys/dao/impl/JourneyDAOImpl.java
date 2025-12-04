package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;
import com.compliancesys.model.ComplianceStatus; // Importar o enum ComplianceStatus

import java.sql.*;
import java.time.Duration;
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
 * Implementação da interface JourneyDAO para operações de persistência da entidade Journey.
 * Interage com o banco de dados PostgreSQL.
 */
public class JourneyDAOImpl implements JourneyDAO {

    private static final Logger LOGGER = Logger.getLogger(JourneyDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public JourneyDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(Journey journey) throws SQLException {
        String sql = "INSERT INTO journeys (driver_id, journey_date, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, journey.getDriverId());
            stmt.setObject(2, journey.getJourneyDate()); // LocalDate
            stmt.setInt(3, (int) journey.getTotalDrivingTime().toMinutes()); // Duration para int minutos
            stmt.setInt(4, (int) journey.getTotalRestTime().toMinutes());   // Duration para int minutos
            stmt.setString(5, journey.getComplianceStatus().name()); // Enum como String
            stmt.setBoolean(6, journey.isDailyLimitExceeded());
            stmt.setObject(7, journey.getCreatedAt());
            stmt.setObject(8, journey.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar jornada: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Journey> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, journey_date, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToJourney(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornada por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException {
        String sql = "SELECT id, driver_id, journey_date, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ? AND journey_date = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            stmt.setObject(2, journeyDate); // LocalDate
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToJourney(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornada por ID do motorista e data: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Journey> findAll() throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_date, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys ORDER BY journey_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                journeys.add(mapResultSetToJourney(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todas as jornadas: " + e.getMessage(), e);
            throw e;
        }
        return journeys;
    }

    @Override
    public List<Journey> findByDriverId(int driverId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_date, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ? ORDER BY journey_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                journeys.add(mapResultSetToJourney(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornadas por ID do motorista: " + e.getMessage(), e);
            throw e;
        }
        return journeys;
    }

    @Override
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, journey_date = ?, total_driving_time_minutes = ?, total_rest_time_minutes = ?, compliance_status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, journey.getDriverId());
            stmt.setObject(2, journey.getJourneyDate());
            stmt.setInt(3, (int) journey.getTotalDrivingTime().toMinutes());
            stmt.setInt(4, (int) journey.getTotalRestTime().toMinutes());
            stmt.setString(5, journey.getComplianceStatus().name());
            stmt.setBoolean(6, journey.isDailyLimitExceeded());
            stmt.setObject(7, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(8, journey.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar jornada: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar jornada: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Journey.
     * @param rs ResultSet contendo os dados da jornada.
     * @return Objeto Journey.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Journey mapResultSetToJourney(ResultSet rs) throws SQLException {
        Journey journey = new Journey();
        journey.setId(rs.getInt("id"));
        journey.setDriverId(rs.getInt("driver_id"));
        journey.setJourneyDate(rs.getObject("journey_date", LocalDate.class)); // LocalDate
        journey.setTotalDrivingTime(Duration.ofMinutes(rs.getInt("total_driving_time_minutes"))); // int minutos para Duration
        journey.setTotalRestTime(Duration.ofMinutes(rs.getInt("total_rest_time_minutes")));     // int minutos para Duration
        journey.setComplianceStatus(ComplianceStatus.valueOf(rs.getString("compliance_status"))); // String para Enum
        journey.setDailyLimitExceeded(rs.getBoolean("daily_limit_exceeded"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        journey.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        journey.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return journey;
    }
}
