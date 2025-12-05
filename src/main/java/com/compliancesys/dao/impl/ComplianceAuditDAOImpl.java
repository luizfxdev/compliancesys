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
import java.util.logging.Logger;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {

    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditDAOImpl.class.getName());

    @Override
    public int create(ComplianceAudit audit) throws SQLException {
        String sql = "INSERT INTO compliance_audits (journey_id, audit_date, compliance_status, auditor_name, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, audit.getJourneyId());
            stmt.setObject(2, audit.getAuditDate());
            stmt.setString(3, audit.getComplianceStatus().name());
            stmt.setString(4, audit.getAuditorName());
            stmt.setString(5, audit.getNotes());
            stmt.setObject(6, audit.getCreatedAt());
            stmt.setObject(7, audit.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar auditoria de conformidade, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar auditoria de conformidade, nenhum ID obtido.");
                }
            }
        }
    }

    @Override
    public Optional<ComplianceAudit> findById(int id) throws SQLException {
        String sql = "SELECT id, journey_id, audit_date, compliance_status, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToComplianceAudit(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ComplianceAudit> findAll() throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, audit_date, compliance_status, auditor_name, notes, created_at, updated_at FROM compliance_audits";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, audit_date, compliance_status, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE journey_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByDriverIdAndDate(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT ca.id, ca.journey_id, ca.audit_date, ca.compliance_status, ca.auditor_name, ca.notes, ca.created_at, ca.updated_at " +
                     "FROM compliance_audits ca JOIN journeys j ON ca.journey_id = j.id " +
                     "WHERE j.driver_id = ? AND ca.audit_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        }
        return audits;
    }

    @Override
    public boolean update(ComplianceAudit audit) throws SQLException {
        String sql = "UPDATE compliance_audits SET journey_id = ?, audit_date = ?, compliance_status = ?, auditor_name = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, audit.getJourneyId());
            stmt.setObject(2, audit.getAuditDate());
            stmt.setString(3, audit.getComplianceStatus().name());
            stmt.setString(4, audit.getAuditorName());
            stmt.setString(5, audit.getNotes());
            stmt.setObject(6, audit.getUpdatedAt());
            stmt.setInt(7, audit.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
        return new ComplianceAudit(
                rs.getInt("id"),
                rs.getInt("journey_id"),
                rs.getObject("audit_date", LocalDateTime.class),
                ComplianceStatus.valueOf(rs.getString("compliance_status")),
                rs.getString("auditor_name"),
                rs.getString("notes"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}