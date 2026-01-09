// src/main/java/com/compliancesys/dao/impl/JourneyDAOImpl.java
package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.model.Journey;

public class JourneyDAOImpl implements JourneyDAO {
    private static final Logger LOGGER = Logger.getLogger(JourneyDAOImpl.class.getName());
    private final Connection connection;

    public JourneyDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(Journey journey) throws SQLException {
        String sql = "INSERT INTO journeys (driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setInt(3, journey.getCompanyId());
            stmt.setObject(4, journey.getJourneyDate());
            stmt.setString(5, journey.getStartLocation());
            stmt.setInt(6, journey.getTotalDrivingTimeMinutes());
            stmt.setInt(7, journey.getTotalRestTimeMinutes());
            stmt.setString(8, journey.getComplianceStatus());
            stmt.setBoolean(9, journey.isDailyLimitExceeded());
            stmt.setObject(10, now);
            stmt.setObject(11, now);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar jornada, nenhuma linha afetada");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar jornada, nenhum ID gerado");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar jornada: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Journey> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJourney(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornada por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Journey> findAll() throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
    public boolean update(Journey journey) throws SQLException {
        String sql = "UPDATE journeys SET driver_id = ?, vehicle_id = ?, company_id = ?, journey_date = ?, start_location = ?, total_driving_time_minutes = ?, total_rest_time_minutes = ?, compliance_status = ?, daily_limit_exceeded = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journey.getDriverId());
            stmt.setInt(2, journey.getVehicleId());
            stmt.setInt(3, journey.getCompanyId());
            stmt.setObject(4, journey.getJourneyDate());
            stmt.setString(5, journey.getStartLocation());
            stmt.setInt(6, journey.getTotalDrivingTimeMinutes());
            stmt.setInt(7, journey.getTotalRestTimeMinutes());
            stmt.setString(8, journey.getComplianceStatus());
            stmt.setBoolean(9, journey.isDailyLimitExceeded());
            stmt.setObject(10, LocalDateTime.now());
            stmt.setInt(11, journey.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar jornada: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journeys WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar jornada: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Journey> findByDriverId(int driverId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornadas por Driver ID: " + e.getMessage(), e);
            throw e;
        }
        return journeys;
    }

    @Override
    public List<Journey> findByVehicleId(int vehicleId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE vehicle_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornadas por Vehicle ID: " + e.getMessage(), e);
            throw e;
        }
        return journeys;
    }

    @Override
    public List<Journey> findByCompanyId(int companyId) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornadas por Company ID: " + e.getMessage(), e);
            throw e;
        }
        return journeys;
    }

    @Override
    public List<Journey> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Journey> journeys = new ArrayList<>();
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE journey_date BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, startDate);
            stmt.setObject(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    journeys.add(mapResultSetToJourney(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornadas por intervalo de datas: " + e.getMessage(), e);
            throw e;
        }
        return journeys;
    }

    @Override
    public Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException {
        String sql = "SELECT id, driver_id, vehicle_id, company_id, journey_date, start_location, total_driving_time_minutes, total_rest_time_minutes, compliance_status, daily_limit_exceeded, created_at, updated_at FROM journeys WHERE driver_id = ? AND journey_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, journeyDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJourney(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar jornada por Driver ID e Data: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    private Journey mapResultSetToJourney(ResultSet rs) throws SQLException {
        return new Journey(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("vehicle_id"),
                rs.getInt("company_id"),
                rs.getObject("journey_date", LocalDate.class),
                rs.getString("start_location"),
                rs.getInt("total_driving_time_minutes"),
                rs.getInt("total_rest_time_minutes"),
                rs.getString("compliance_status"),
                rs.getBoolean("daily_limit_exceeded"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}