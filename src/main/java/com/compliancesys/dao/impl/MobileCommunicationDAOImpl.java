package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.model.MobileCommunication;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MobileCommunicationDAOImpl implements MobileCommunicationDAO {

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
                }
            }
        }
        return -1; // Indica falha na inserção
    }

    @Override
    public Optional<MobileCommunication> findById(int id) throws SQLException {
        String sql = "SELECT * FROM mobile_communications WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
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
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                communications.add(mapResultSetToMobileCommunication(rs));
            }
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByDriverId(int driverId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT * FROM mobile_communications WHERE driver_id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
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
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communication.getDriverId());
            stmt.setInt(2, communication.getRecordId());
            stmt.setObject(3, communication.getTimestamp());
            stmt.setObject(4, communication.getLatitude());
            stmt.setObject(5, communication.getLongitude());
            stmt.setObject(6, communication.getSendTimestamp());
            stmt.setBoolean(7, communication.isSendSuccess());
            stmt.setString(8, communication.getErrorMessage());
            stmt.setObject(9, LocalDateTime.now());
            stmt.setInt(10, communication.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mobile_communications WHERE id = ?";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private MobileCommunication mapResultSetToMobileCommunication(ResultSet rs) throws SQLException {
        return new MobileCommunication(
                rs.getInt("id"),
                rs.getInt("driver_id"),
                rs.getInt("record_id"),
                rs.getObject("timestamp", LocalDateTime.class),
                rs.getObject("latitude", Double.class),
                rs.getObject("longitude", Double.class),
                rs.getObject("send_timestamp", LocalDateTime.class),
                rs.getBoolean("send_success"),
                rs.getString("error_message"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
