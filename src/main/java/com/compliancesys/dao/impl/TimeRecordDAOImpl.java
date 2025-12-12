package com.compliancesys.dao.impl;

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.EventType; // Importa o enum EventType
import com.compliancesys.model.TimeRecord;
import com.compliancesys.config.DatabaseConfig; // Temporário para DataSource
import javax.sql.DataSource; // Importa DataSource
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
<<<<<<< Updated upstream

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
=======
>>>>>>> Stashed changes

/**
 * Implementação do Data Access Object (DAO) para a entidade TimeRecord.
 * Gerencia a persistência de registros de ponto no banco de dados.
 */
public class TimeRecordDAOImpl implements TimeRecordDAO {

<<<<<<< Updated upstream
    private final Connection connection;

    public TimeRecordDAOImpl(Connection connection) {
        this.connection = connection;
=======
    private final DataSource dataSource; // Adicionado DataSource

    // Construtor para injeção de dependência do DataSource
    public TimeRecordDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
>>>>>>> Stashed changes
    }

    @Override
    public int create(TimeRecord timeRecord) throws SQLException {
<<<<<<< Updated upstream
        String sql = "INSERT INTO time_records (driver_id, record_time, event_type, location, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setTimestamp(2, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(3, timeRecord.getEventType().name()); // Converte enum para String
            stmt.setString(4, timeRecord.getLocation());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
=======
        String sql = "INSERT INTO time_records (driver_id, vehicle_id, record_time, event_type, location, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, timeRecord.getDriverId());
            // Verifica se vehicleId é null antes de setar
            if (timeRecord.getVehicleId() != null) {
                stmt.setInt(2, timeRecord.getVehicleId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setObject(3, timeRecord.getRecordTime());
            stmt.setString(4, timeRecord.getEventType().name()); // Converte enum para String
            stmt.setString(5, timeRecord.getLocation());
            stmt.setObject(6, LocalDateTime.now());
            stmt.setObject(7, LocalDateTime.now());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar registro de ponto, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar registro de ponto, nenhum ID gerado.");
>>>>>>> Stashed changes
                }
            }
        }
        throw new SQLException("Falha ao criar registro de ponto, nenhum ID gerado.");
    }

    @Override
    public Optional<TimeRecord> findById(int id) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE id = ?";
<<<<<<< Updated upstream
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
=======
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

>>>>>>> Stashed changes
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TimeRecord> findAll() throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
<<<<<<< Updated upstream
        String sql = "SELECT * FROM time_records ORDER BY record_time ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
=======
        String sql = "SELECT * FROM time_records";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

>>>>>>> Stashed changes
            while (rs.next()) {
                timeRecords.add(mapResultSetToTimeRecord(rs));
            }
        }
        return timeRecords;
    }

    @Override
<<<<<<< Updated upstream
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, record_time = ?, event_type = ?, location = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, timeRecord.getDriverId());
            stmt.setTimestamp(2, Timestamp.valueOf(timeRecord.getRecordTime()));
            stmt.setString(3, timeRecord.getEventType().name()); // Converte enum para String
            stmt.setString(4, timeRecord.getLocation());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, timeRecord.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? ORDER BY record_time ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
=======
    public List<TimeRecord> findByDriverId(int driverId) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? ORDER BY record_time ASC";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
    public List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? AND DATE(record_time) = ? ORDER BY record_time ASC";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            stmt.setObject(2, date); // LocalDate é mapeado diretamente
>>>>>>> Stashed changes
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
        }
        return timeRecords;
    }

    @Override
<<<<<<< Updated upstream
    public List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM time_records WHERE driver_id = ? AND DATE(record_time) = ? ORDER BY record_time ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeRecords.add(mapResultSetToTimeRecord(rs));
                }
            }
=======
    public boolean update(TimeRecord timeRecord) throws SQLException {
        String sql = "UPDATE time_records SET driver_id = ?, vehicle_id = ?, record_time = ?, event_type = ?, location = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, timeRecord.getDriverId());
            // Verifica se vehicleId é null antes de setar
            if (timeRecord.getVehicleId() != null) {
                stmt.setInt(2, timeRecord.getVehicleId());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setObject(3, timeRecord.getRecordTime());
            stmt.setString(4, timeRecord.getEventType().name()); // Converte enum para String
            stmt.setString(5, timeRecord.getLocation());
            stmt.setObject(6, LocalDateTime.now()); // Atualiza updated_at
            stmt.setInt(7, timeRecord.getId());
            return stmt.executeUpdate() > 0;
>>>>>>> Stashed changes
        }
        return timeRecords;
    }

    @Override
<<<<<<< Updated upstream
    public Optional<TimeRecord> findByDriverIdAndRecordTimeAndEventType(int driverId, LocalDateTime recordTime, String eventType) throws SQLException {
        String sql = "SELECT * FROM time_records WHERE driver_id = ? AND record_time = ? AND event_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setTimestamp(2, Timestamp.valueOf(recordTime));
            stmt.setString(3, eventType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimeRecord(rs));
                }
            }
=======
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_records WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
>>>>>>> Stashed changes
        }
        return Optional.empty();
    }

    @Override
    public List<TimeRecord> findByJourneyId(int journeyId) throws SQLException {
        // Nota: A tabela time_records no schema.sql não possui journey_id
        // Esta é uma limitação do design atual do banco de dados
        // Para buscar TimeRecords de uma Journey, use findByDriverIdAndDate com os dados da Journey
        return new ArrayList<>();
    }

    private TimeRecord mapResultSetToTimeRecord(ResultSet rs) throws SQLException {
<<<<<<< Updated upstream
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setId(rs.getInt("id"));
        timeRecord.setDriverId(rs.getInt("driver_id"));
        timeRecord.setRecordTime(rs.getTimestamp("record_time").toLocalDateTime());
        
        // Converte String do banco para enum EventType
        String eventTypeStr = rs.getString("event_type");
        timeRecord.setEventType(EventType.valueOf(eventTypeStr));
        
        timeRecord.setLocation(rs.getString("location"));
        timeRecord.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        timeRecord.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return timeRecord;
=======
        return new TimeRecord(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getObject("vehicle_id", Integer.class), // Mapeia para Integer para permitir null
                rs.getObject("record_time", LocalDateTime.class),
                EventType.valueOf(rs.getString("event_type")), // Converte String para enum
                rs.getString("location"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
>>>>>>> Stashed changes
    }
}