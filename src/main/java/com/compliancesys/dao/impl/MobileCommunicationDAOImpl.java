// src/main/java/com/compliancesys/dao/impl/MobileCommunicationDAOImpl.java
package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.enums.EventType;

public class MobileCommunicationDAOImpl implements MobileCommunicationDAO {
    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationDAOImpl.class.getName());
    private final Connection connection;

    public MobileCommunicationDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(MobileCommunication communication) throws SQLException {
        String sql = "INSERT INTO mobile_communications (driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getJourneyId());
            stmt.setObject(3, communication.getTimestamp());
            stmt.setObject(4, communication.getLatitude());
            stmt.setObject(5, communication.getLongitude());
            stmt.setString(6, communication.getEventType() != null ? communication.getEventType().name() : null);
            stmt.setString(7, communication.getDeviceId());
            stmt.setObject(8, now);
            stmt.setObject(9, now);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar comunicação móvel, nenhuma linha afetada");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar comunicação móvel, nenhum ID gerado");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<MobileCommunication> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at FROM mobile_communications WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicação móvel por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<MobileCommunication> findAll() throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at FROM mobile_communications";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                communications.add(mapResultSetToMobileCommunication(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todas as comunicações móveis: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public boolean update(MobileCommunication communication) throws SQLException {
        String sql = "UPDATE mobile_communications SET driver_id = ?, journey_id = ?, timestamp = ?, latitude = ?, longitude = ?, event_type = ?, device_id = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getJourneyId());
            stmt.setObject(3, communication.getTimestamp());
            stmt.setObject(4, communication.getLatitude());
            stmt.setObject(5, communication.getLongitude());
            stmt.setString(6, communication.getEventType() != null ? communication.getEventType().name() : null);
            stmt.setString(7, communication.getDeviceId());
            stmt.setObject(8, LocalDateTime.now());
            stmt.setInt(9, communication.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mobile_communications WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<MobileCommunication> findByJourneyId(int journeyId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at FROM mobile_communications WHERE journey_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por Journey ID: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByDriverId(int driverId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at FROM mobile_communications WHERE driver_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por Driver ID: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByEventType(EventType eventType) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at FROM mobile_communications WHERE event_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, eventType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por tipo de evento: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, timestamp, latitude, longitude, event_type, device_id, created_at, updated_at FROM mobile_communications WHERE timestamp BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, startDateTime);
            stmt.setObject(2, endDateTime);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por intervalo de data/hora: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    private MobileCommunication mapResultSetToMobileCommunication(ResultSet rs) throws SQLException {
        String eventTypeString = rs.getString("event_type");
        EventType eventType = eventTypeString != null ? EventType.valueOf(eventTypeString) : null;

        return new MobileCommunication(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("journey_id"),
                rs.getObject("timestamp", LocalDateTime.class),
                rs.getObject("latitude", Double.class),
                rs.getObject("longitude", Double.class),
                eventType,
                rs.getString("device_id"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}