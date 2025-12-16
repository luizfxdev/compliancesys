package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.util.ConnectionFactory;

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {

    private final ConnectionFactory connectionFactory;

    public ComplianceAuditDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ComplianceAudit create(ComplianceAudit audit) throws SQLException {
        String sql = "INSERT INTO compliance_audits (journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration_minutes, max_continuous_driving_minutes, auditor_name, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, audit.getJourneyId());
            stmt.setInt(2, audit.getDriverId());
            stmt.setDate(3, audit.getAuditDate() != null ? Date.valueOf(audit.getAuditDate()) : null);
            stmt.setTimestamp(4, audit.getAuditTimestamp() != null ? Timestamp.valueOf(audit.getAuditTimestamp()) : null);
            stmt.setString(5, audit.getStatus() != null ? audit.getStatus().name() : null);
            stmt.setString(6, audit.getViolations());
            stmt.setObject(7, audit.getTotalWorkDuration() != null ? audit.getTotalWorkDuration().toMinutes() : null, Types.BIGINT);
            stmt.setObject(8, audit.getMaxContinuousDriving() != null ? audit.getMaxContinuousDriving().toMinutes() : null, Types.BIGINT);
            stmt.setString(9, audit.getAuditorName());
            stmt.setString(10, audit.getNotes());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar auditoria, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    audit.setId(generatedKeys.getInt(1));
                    audit.setCreatedAt(LocalDateTime.now());
                    audit.setUpdatedAt(LocalDateTime.now());
                } else {
                    throw new SQLException("Falha ao criar auditoria, nenhum ID obtido.");
                }
            }
            return audit;
        }
    }

    @Override
    public Optional<ComplianceAudit> findById(int id) throws SQLException {
        String sql = "SELECT * FROM compliance_audits WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
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
    public List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits WHERE journey_id = ?";
        try (Connection conn = connectionFactory.getConnection();
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
    public List<ComplianceAudit> findByDriverId(int driverId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits WHERE driver_id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
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
        String sql = "SELECT * FROM compliance_audits WHERE audit_date BETWEEN ? AND ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByStatus(ComplianceStatus status) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits WHERE status = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findAll() throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        }
        return audits;
    }

    @Override
    public boolean update(ComplianceAudit audit) throws SQLException {
        String sql = "UPDATE compliance_audits SET journey_id = ?, driver_id = ?, audit_date = ?, audit_timestamp = ?, status = ?, violations = ?, total_work_duration_minutes = ?, max_continuous_driving_minutes = ?, auditor_name = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, audit.getJourneyId());
            stmt.setInt(2, audit.getDriverId());
            stmt.setDate(3, audit.getAuditDate() != null ? Date.valueOf(audit.getAuditDate()) : null);
            stmt.setTimestamp(4, audit.getAuditTimestamp() != null ? Timestamp.valueOf(audit.getAuditTimestamp()) : null);
            stmt.setString(5, audit.getStatus() != null ? audit.getStatus().name() : null);
            stmt.setString(6, audit.getViolations());
            stmt.setObject(7, audit.getTotalWorkDuration() != null ? audit.getTotalWorkDuration().toMinutes() : null, Types.BIGINT);
            stmt.setObject(8, audit.getMaxContinuousDriving() != null ? audit.getMaxContinuousDriving().toMinutes() : null, Types.BIGINT);
            stmt.setString(9, audit.getAuditorName());
            stmt.setString(10, audit.getNotes());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(12, audit.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int journeyId = rs.getInt("journey_id");
        int driverId = rs.getInt("driver_id");
        LocalDate auditDate = rs.getDate("audit_date") != null ? rs.getDate("audit_date").toLocalDate() : null;
        LocalDateTime auditTimestamp = rs.getTimestamp("audit_timestamp") != null ? rs.getTimestamp("audit_timestamp").toLocalDateTime() : null;
        String statusString = rs.getString("status");
        ComplianceStatus status = statusString != null ? ComplianceStatus.valueOf(statusString) : null;
        String violations = rs.getString("violations");
        Duration totalWorkDuration = rs.getObject("total_work_duration_minutes", Long.class) != null ? Duration.ofMinutes(rs.getLong("total_work_duration_minutes")) : null;
        Duration maxContinuousDriving = rs.getObject("max_continuous_driving_minutes", Long.class) != null ? Duration.ofMinutes(rs.getLong("max_continuous_driving_minutes")) : null;
        String auditorName = rs.getString("auditor_name");
        String notes = rs.getString("notes");
        LocalDateTime createdAt = rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null;
        LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

        ComplianceAudit audit = new ComplianceAudit(id, journeyId, driverId, auditDate, auditTimestamp, status,
                                                    violations, totalWorkDuration, maxContinuousDriving,
                                                    auditorName, notes, createdAt, updatedAt);
        audit.setComplianceStatus(statusString); // Garante que a string interna esteja sincronizada
        return audit;
    }
}
