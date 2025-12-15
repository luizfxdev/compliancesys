// src/main/java/com/compliancesys/dao/impl/TimeRecordDAOImpl.java
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

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;

public class TimeRecordDAOImpl implements TimeRecordDAO {
    private static final Logger LOGGER = Logger.getLogger(TimeRecordDAOImpl.class.getName());
    private final Connection connection;

    public TimeRecordDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(TimeRecord timeRecord) throws SQLException {
        // Adicionado latitude e longitude ao SQL de inserção
        String sql = "INSERT INTO time_records (driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getJourneyId());
            stmt.setObject(3, timeRecord.getRecordTime()); // Usar getRecordTime()
            stmt.setString(4, timeRecord.getEventType().name());
            stmt.setString(5, timeRecord.getLocation());
            // Mapear latitude e longitude, permitindo nulos
            if (timeRecord.getLatitude() != null) {
                stmt.setDouble(6, timeRecord.getLatitude());
            } else {
                stmt.setNull(6, java.sql.Types.DOUBLE);
            }
            if (timeRecord.getLongitude() != null) {
                stmt.setDouble(7, timeRecord.getLongitude());
            } else {
                stmt.setNull(7, java.sql.Types.DOUBLE);
            }
            stmt.setObject(8, now);
            stmt.setObject(9, now);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar registro de tempo, nenhuma linha afetada.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar registro de tempo, nenhum ID gerado.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de tempo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de tempo por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<TimeRecord> findAll() throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os registros de tempo: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public boolean update(TimeRecord timeRecord) throws SQLException {
        // Adicionado latitude e longitude ao SQL de atualização
        String sql = "UPDATE time_records SET driver_id = ?, journey_id = ?, record_time = ?, event_type = ?, location = ?, latitude = ?, longitude = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setInt(2, timeRecord.getJourneyId());
            stmt.setObject(3, timeRecord.getRecordTime()); // Usar getRecordTime()
            stmt.setString(4, timeRecord.getEventType().name());
            stmt.setString(5, timeRecord.getLocation());
            // Mapear latitude e longitude, permitindo nulos
            if (timeRecord.getLatitude() != null) {
                stmt.setDouble(6, timeRecord.getLatitude());
            } else {
                stmt.setNull(6, java.sql.Types.DOUBLE);
            }
            if (timeRecord.getLongitude() != null) {
                stmt.setDouble(7, timeRecord.getLongitude());
            } else {
                stmt.setNull(7, java.sql.Types.DOUBLE);
            }
            stmt.setObject(8, LocalDateTime.now());
            stmt.setInt(9, timeRecord.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar registro de tempo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar registro de tempo: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<TimeRecord> findByJourneyId(int journeyId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE journey_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por ID da jornada: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE driver_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por ID do motorista: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByEventType(EventType eventType) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE event_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, eventType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por tipo de evento: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE driver_id = ? AND DATE(record_time) BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por motorista e intervalo de datas: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByJourneyIdOrderedByTimestamp(int journeyId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        // Ordena por record_time em ordem crescente para que o último elemento seja o mais recente
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE journey_id = ? ORDER BY record_time ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por ID da jornada ordenados por timestamp: " + e.getMessage(), e);
            throw e;
        }
        return timeRecords;
    }

    @Override
    public Optional<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException {
        String sql = "SELECT id, driver_id, journey_id, record_time, event_type, location, latitude, longitude, created_at, updated_at FROM time_records WHERE driver_id = ? AND DATE(record_time) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, date);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de tempo por motorista e data: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
        // Mapear latitude e longitude, tratando possíveis nulos do banco de dados
        Double latitude = rs.getObject("latitude", Double.class);
        Double longitude = rs.getObject("longitude", Double.class);

        return new TimeRecord(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("journey_id"),
                rs.getObject("record_time", LocalDateTime.class), // Usar record_time
                EventType.valueOf(rs.getString("event_type")),
                rs.getString("location"),
                latitude, // Passar latitude
                longitude, // Passar longitude
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
