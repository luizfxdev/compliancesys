package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.util.DatabaseConnection;

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {

    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditDAOImpl.class.getName());

    @Override
    public int create(ComplianceAudit audit) throws SQLException {
        String sql = "INSERT INTO compliance_audits (journey_id, audit_date, compliance_status, auditor_name, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setTimestamp(2, Timestamp.valueOf(audit.getAuditDate())); // audit.getAuditDate() já é LocalDateTime
            stmt.setString(3, audit.getComplianceStatus().name());
            stmt.setString(4, audit.getAuditorName());
            stmt.setString(5, audit.getNotes());
            stmt.setTimestamp(6, Timestamp.valueOf(audit.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(audit.getUpdatedAt()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar auditoria de conformidade, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar auditoria de conformidade, nenhum ID gerado.");
                }
            }
        }
    }

    @Override
    public Optional<ComplianceAudit> findById(int id) throws SQLException {
        String sql = "SELECT * FROM compliance_audits WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
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
        String sql = "SELECT * FROM compliance_audits ORDER BY audit_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        }
        return audits;
    }

    @Override
    public boolean update(ComplianceAudit audit) throws SQLException { // Retorno boolean
        String sql = "UPDATE compliance_audits SET journey_id = ?, audit_date = ?, compliance_status = ?, auditor_name = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setTimestamp(2, Timestamp.valueOf(audit.getAuditDate())); // audit.getAuditDate() já é LocalDateTime
            stmt.setString(3, audit.getComplianceStatus().name());
            stmt.setString(4, audit.getAuditorName());
            stmt.setString(5, audit.getNotes());
            stmt.setTimestamp(6, Timestamp.valueOf(audit.getUpdatedAt()));
            stmt.setInt(7, audit.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits WHERE journey_id = ? ORDER BY audit_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
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
    public List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits WHERE audit_date BETWEEN ? AND ? ORDER BY audit_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Para comparar com TIMESTAMP no banco, precisamos de LocalDateTime
            // startDate.atStartOfDay() e endDate.atTime(23, 59, 59) para cobrir o dia inteiro
            stmt.setTimestamp(1, Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate.atTime(23, 59, 59)));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        }
        return audits;
    }

    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
        ComplianceAudit audit = new ComplianceAudit();
        audit.setId(rs.getInt("id"));
        audit.setJourneyId(rs.getInt("journey_id"));
        audit.setAuditDate(rs.getTimestamp("audit_date").toLocalDateTime()); // Correção: remove .toLocalDate()
        audit.setComplianceStatus(ComplianceStatus.valueOf(rs.getString("compliance_status")));
        audit.setAuditorName(rs.getString("auditor_name"));
        audit.setNotes(rs.getString("notes"));
        audit.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        audit.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return audit;
    }
}
