package com.compliancesys.dao.impl;

import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.model.MobileCommunication;

<<<<<<< Updated upstream
import java.sql.*;
=======
import javax.sql.DataSource; // Importa DataSource
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
>>>>>>> Stashed changes
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do Data Access Object (DAO) para a entidade MobileCommunication.
 * Gerencia a persistência de dados de comunicações móveis no banco de dados.
 */
public class MobileCommunicationDAOImpl implements MobileCommunicationDAO {

<<<<<<< Updated upstream
    @Override
    public int create(MobileCommunication communication) throws SQLException {
        String sql = "INSERT INTO mobile_communications (driver_id, record_id, timestamp, latitude, longitude, send_timestamp, send_success, error_message, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getRecordId());
            stmt.setObject(3, communication.getTimestamp());
            stmt.setObject(4, communication.getLatitude());
            stmt.setObject(5, communication.getLongitude());
            stmt.setObject(6, communication.getSendTimestamp());
            stmt.setBoolean(7, communication.isSendSuccess());
            stmt.setString(8, communication.getErrorMessage());
            stmt.setObject(9, LocalDateTime.now());
            stmt.setObject(10, LocalDateTime.now());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
=======
    private final DataSource dataSource; // Adicionado DataSource

    // Construtor que recebe o DataSource
    public MobileCommunicationDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int create(MobileCommunication communication) throws SQLException {
        String sql = "INSERT INTO mobile_communications (driver_id, record_id, timestamp, latitude, longitude, send_timestamp, send_success, error_message, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();

            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getRecordId());
            stmt.setObject(3, communication.getTimestamp());
            // Para Double, use setDouble ou setObject. Se puder ser null, setObject é mais seguro.
            if (communication.getLatitude() != null) {
                stmt.setDouble(4, communication.getLatitude());
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }
            if (communication.getLongitude() != null) {
                stmt.setDouble(5, communication.getLongitude());
            } else {
                stmt.setNull(5, java.sql.Types.DOUBLE);
            }
            stmt.setObject(6, communication.getSendTimestamp());
            stmt.setBoolean(7, communication.isSendSuccess());
            stmt.setString(8, communication.getErrorMessage());
            stmt.setObject(9, now); // created_at
            stmt.setObject(10, now); // updated_at

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar comunicação móvel, nenhum registro afetado.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar comunicação móvel, nenhum ID gerado.");
>>>>>>> Stashed changes
                }
            }
        }
        return -1; // Indica falha na inserção
    }

    @Override
    public Optional<MobileCommunication> findById(int id) throws SQLException {
        String sql = "SELECT * FROM mobile_communications WHERE id = ?";
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMobileCommunication(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<MobileCommunication> findAll() throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT * FROM mobile_communications";
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                communications.add(mapResultSetToMobileCommunication(rs));
            }
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByRecordId(int recordId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
<<<<<<< Updated upstream
        String sql = "SELECT * FROM mobile_communications WHERE driver_id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        String sql = "SELECT * FROM mobile_communications WHERE record_id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByRecordId(int recordId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT * FROM mobile_communications WHERE record_id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    communications.add(mapResultSetToMobileCommunication(rs));
                }
            }
        }
        return communications;
    }

    @Override
    public boolean update(MobileCommunication communication) throws SQLException {
        String sql = "UPDATE mobile_communications SET driver_id = ?, record_id = ?, timestamp = ?, latitude = ?, longitude = ?, send_timestamp = ?, send_success = ?, error_message = ?, updated_at = ? WHERE id = ?";
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getRecordId());
            stmt.setObject(3, communication.getTimestamp());
<<<<<<< Updated upstream
            stmt.setObject(4, communication.getLatitude());
            stmt.setObject(5, communication.getLongitude());
            stmt.setObject(6, communication.getSendTimestamp());
            stmt.setBoolean(7, communication.isSendSuccess());
            stmt.setString(8, communication.getErrorMessage());
            stmt.setObject(9, LocalDateTime.now());
=======
            if (communication.getLatitude() != null) {
                stmt.setDouble(4, communication.getLatitude());
            } else {
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }
            if (communication.getLongitude() != null) {
                stmt.setDouble(5, communication.getLongitude());
            } else {
                stmt.setNull(5, java.sql.Types.DOUBLE);
            }
            stmt.setObject(6, communication.getSendTimestamp());
            stmt.setBoolean(7, communication.isSendSuccess());
            stmt.setString(8, communication.getErrorMessage());
            stmt.setObject(9, LocalDateTime.now()); // updated_at
>>>>>>> Stashed changes
            stmt.setInt(10, communication.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mobile_communications WHERE id = ?";
<<<<<<< Updated upstream
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
=======
        try (Connection conn = dataSource.getConnection(); // Usa DataSource
>>>>>>> Stashed changes
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private MobileCommunication mapResultSetToMobileCommunication(ResultSet rs) throws SQLException {
        // Para Double, é importante verificar se o valor é nulo no banco de dados
        Double latitude = rs.getObject("latitude", Double.class);
        Double longitude = rs.getObject("longitude", Double.class);

        return new MobileCommunication(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("record_id"),
                rs.getObject("timestamp", LocalDateTime.class),
<<<<<<< Updated upstream
                rs.getObject("latitude", Double.class),
                rs.getObject("longitude", Double.class),
=======
                latitude,
                longitude,
>>>>>>> Stashed changes
                rs.getObject("send_timestamp", LocalDateTime.class),
                rs.getBoolean("send_success"),
                rs.getString("error_message"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
