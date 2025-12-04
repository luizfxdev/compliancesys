package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.model.MobileCommunication;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface MobileCommunicationDAO para operações de persistência da entidade MobileCommunication.
 * Interage com o banco de dados PostgreSQL.
 */
public class MobileCommunicationDAOImpl implements MobileCommunicationDAO {

    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public MobileCommunicationDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(MobileCommunication communication) throws SQLException {
        String sql = "INSERT INTO mobile_communications (record_id, send_timestamp, send_success, error_message, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, communication.getRecordId());
            stmt.setObject(2, communication.getSendTimestamp()); // LocalDateTime
            stmt.setBoolean(3, communication.isSendSuccess());
            stmt.setString(4, communication.getErrorMessage());
            stmt.setObject(5, communication.getCreatedAt());
            stmt.setObject(6, communication.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar registro de comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<MobileCommunication> findById(int id) throws SQLException {
        String sql = "SELECT id, record_id, send_timestamp, send_success, error_message, created_at, updated_at FROM mobile_communications WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMobileCommunication(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar registro de comunicação móvel por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<MobileCommunication> findAll() throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, record_id, send_timestamp, send_success, error_message, created_at, updated_at FROM mobile_communications ORDER BY send_timestamp DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                communications.add(mapResultSetToMobileCommunication(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todos os registros de comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public List<MobileCommunication> findByRecordId(int recordId) throws SQLException {
        List<MobileCommunication> communications = new ArrayList<>();
        String sql = "SELECT id, record_id, send_timestamp, send_success, error_message, created_at, updated_at FROM mobile_communications WHERE record_id = ? ORDER BY send_timestamp DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                communications.add(mapResultSetToMobileCommunication(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar registros de comunicação móvel por ID do registro de ponto: " + e.getMessage(), e);
            throw e;
        }
        return communications;
    }

    @Override
    public boolean update(MobileCommunication communication) throws SQLException {
        String sql = "UPDATE mobile_communications SET record_id = ?, send_timestamp = ?, send_success = ?, error_message = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, communication.getRecordId());
            stmt.setObject(2, communication.getSendTimestamp());
            stmt.setBoolean(3, communication.isSendSuccess());
            stmt.setString(4, communication.getErrorMessage());
            stmt.setObject(5, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(6, communication.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar registro de comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mobile_communications WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar registro de comunicação móvel: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto MobileCommunication.
     * @param rs ResultSet contendo os dados da comunicação móvel.
     * @return Objeto MobileCommunication.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private MobileCommunication mapResultSetToMobileCommunication(ResultSet rs) throws SQLException {
        MobileCommunication communication = new MobileCommunication();
        communication.setId(rs.getInt("id"));
        communication.setRecordId(rs.getInt("record_id"));
        communication.setSendTimestamp(rs.getObject("send_timestamp", LocalDateTime.class)); // LocalDateTime
        communication.setSendSuccess(rs.getBoolean("send_success"));
        communication.setErrorMessage(rs.getString("error_message"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        communication.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        communication.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return communication;
    }
}
