package com.compliancesys.dao.impl;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceStatus; // Importar o enum ComplianceStatus

import java.sql.*;
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
 * Implementação da interface ComplianceAuditDAO para operações de persistência da entidade ComplianceAudit.
 * Interage com o banco de dados PostgreSQL.
 */
public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {

    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditDAOImpl.class.getName());
    private final DatabaseConfig dbConfig;

    public ComplianceAuditDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public int create(ComplianceAudit audit) throws SQLException {
        String sql = "INSERT INTO compliance_audits (journey_id, audit_date, status, details, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, audit.getJourneyId());
            stmt.setObject(2, audit.getAuditDate()); // LocalDateTime
            stmt.setString(3, audit.getStatus().name()); // Enum como String
            stmt.setString(4, audit.getDetails());
            stmt.setObject(5, audit.getCreatedAt());
            stmt.setObject(6, audit.getUpdatedAt());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1; // Indica falha na criação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar auditoria de conformidade: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<ComplianceAudit> findById(int id) throws SQLException {
        String sql = "SELECT id, journey_id, audit_date, status, details, created_at, updated_at FROM compliance_audits WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToComplianceAudit(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditoria de conformidade por ID: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<ComplianceAudit> findAll() throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, audit_date, status, details, created_at, updated_at FROM compliance_audits ORDER BY audit_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todas as auditorias de conformidade: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, audit_date, status, details, created_at, updated_at FROM compliance_audits WHERE journey_id = ? ORDER BY audit_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, journeyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias de conformidade por ID da jornada: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT ca.id, ca.journey_id, ca.audit_date, ca.status, ca.details, ca.created_at, ca.updated_at " +
                     "FROM compliance_audits ca " +
                     "JOIN journeys j ON ca.journey_id = j.id " +
                     "WHERE j.driver_id = ? AND j.journey_date >= ? AND j.journey_date <= ? " +
                     "ORDER BY ca.audit_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, driverId);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias de conformidade por motorista e período: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, audit_date, status, details, created_at, updated_at " +
                     "FROM compliance_audits " +
                     "WHERE audit_date >= ? AND audit_date < ? " + // Usando < para cobrir o dia inteiro
                     "ORDER BY audit_date DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, startDate.atStartOfDay());
            stmt.setObject(2, endDate.plusDays(1).atStartOfDay()); // Para incluir o último dia completo
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias de conformidade por período: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public boolean update(ComplianceAudit audit) throws SQLException {
        String sql = "UPDATE compliance_audits SET journey_id = ?, audit_date = ?, status = ?, details = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, audit.getJourneyId());
            stmt.setObject(2, audit.getAuditDate());
            stmt.setString(3, audit.getStatus().name());
            stmt.setString(4, audit.getDetails());
            stmt.setObject(5, LocalDateTime.now()); // Atualiza o updated_at automaticamente
            stmt.setInt(6, audit.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar auditoria de conformidade: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar auditoria de conformidade: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Mapeia um ResultSet para um objeto ComplianceAudit.
     * @param rs ResultSet contendo os dados da auditoria.
     * @return Objeto ComplianceAudit.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
        ComplianceAudit audit = new ComplianceAudit();
        audit.setId(rs.getInt("id"));
        audit.setJourneyId(rs.getInt("journey_id"));
        audit.setAuditDate(rs.getObject("audit_date", LocalDateTime.class)); // LocalDateTime
        audit.setStatus(ComplianceStatus.valueOf(rs.getString("status"))); // String para Enum
        audit.setDetails(rs.getString("details"));

        // Converte OffsetDateTime (timestamptz) para LocalDateTime
        OffsetDateTime createdAtOffset = rs.getObject("created_at", OffsetDateTime.class);
        audit.setCreatedAt(createdAtOffset != null ? createdAtOffset.toLocalDateTime() : null);

        OffsetDateTime updatedAtOffset = rs.getObject("updated_at", OffsetDateTime.class);
        audit.setUpdatedAt(updatedAtOffset != null ? updatedAtOffset.toLocalDateTime() : null);

        return audit;
    }
}
