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

import com.compliancesys.dao.MobileCommunicationDAO; // CORRIGIDO: Pacote ConnectionFactory
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.util.ConnectionFactory;

public class MobileCommunicationDAOImpl implements MobileCommunicationDAO {
    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationDAOImpl.class.getName());
    private final ConnectionFactory connectionFactory;

    public MobileCommunicationDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public int create(MobileCommunication communication) throws SQLException {
        String sql = "INSERT INTO mobile_communications (driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getJourneyId());
            stmt.setString(3, communication.getEventType().name());
            stmt.setObject(4, communication.getTimestamp());
            stmt.setDouble(5, communication.getLatitude());
            stmt.setDouble(6, communication.getLongitude());
            stmt.setInt(7, communication.getSignalStrength()); // CORRIGIDO: Agora o modelo tem este método
            stmt.setInt(8, communication.getBatteryLevel());   // CORRIGIDO: Agora o modelo tem este método
            stmt.setObject(9, communication.getCreatedAt() != null ? communication.getCreatedAt() : LocalDateTime.now());
            stmt.setObject(10, communication.getUpdatedAt() != null ? communication.getUpdatedAt() : LocalDateTime.now());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar comunicação móvel, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar comunicação móvel, nenhum ID gerado.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<MobileCommunication> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at FROM mobile_communications WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT id, driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at FROM mobile_communications";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
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
        String sql = "UPDATE mobile_communications SET driver_id = ?, journey_id = ?, event_type = ?, timestamp = ?, latitude = ?, longitude = ?, signal_strength = ?, battery_level = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getJourneyId());
            stmt.setString(3, communication.getEventType().name());
            stmt.setObject(4, communication.getTimestamp());
            stmt.setDouble(5, communication.getLatitude());
            stmt.setDouble(6, communication.getLongitude());
            stmt.setInt(7, communication.getSignalStrength()); // CORRIGIDO: Agora o modelo tem este método
            stmt.setInt(8, communication.getBatteryLevel());   // CORRIGIDO: Agora o modelo tem este método
            stmt.setObject(9, LocalDateTime.now());
            stmt.setInt(10, communication.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mobile_communications WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT id, driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at FROM mobile_communications WHERE journey_id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por ID de jornada: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByDriverId(int driverId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at FROM mobile_communications WHERE driver_id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por ID de motorista: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByEventType(EventType eventType) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at FROM mobile_communications WHERE event_type = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
    public List<MobileCommunication> findByDateTimeRange(LocalDateTime start, LocalDateTime end) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, event_type, timestamp, latitude, longitude, signal_strength, battery_level, created_at, updated_at FROM mobile_communications WHERE timestamp BETWEEN ? AND ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, start);
            stmt.setObject(2, end);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar comunicações móveis por range de data/hora: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    private MobileCommunication mapResultSetToMobileCommunication(ResultSet rs) throws SQLException {
        return new MobileCommunication(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("journey_id"),
                rs.getObject("timestamp", LocalDateTime.class),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                EventType.valueOf(rs.getString("event_type")),
                rs.getString("device_id"),
                rs.getInt("signal_strength"), // CORRIGIDO: Mapeando signal_strength
                rs.getInt("battery_level"),   // CORRIGIDO: Mapeando battery_level
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
