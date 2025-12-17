package com.compliancesys.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.util.ConnectionFactory; 

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {
    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditDAOImpl.class.getName());
    private final ConnectionFactory connectionFactory; 

    public ComplianceAuditDAOImpl(ConnectionFactory connectionFactory) { // Alterado o construtor
        this.connectionFactory = connectionFactory;
    }

    @Override
    public int create(ComplianceAudit audit) throws SQLException {
        String sql = "INSERT INTO compliance_audits (journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionFactory.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, audit.getJourneyId());
            stmt.setInt(2, audit.getDriverId());
            stmt.setObject(3, audit.getAuditDate());
            stmt.setObject(4, audit.getAuditTimestamp());
            stmt.setString(5, audit.getStatus().name()); 
            stmt.setString(6, audit.getViolations());
            stmt.setLong(7, audit.getTotalWorkDuration() != null ? audit.getTotalWorkDuration().toMinutes() : 0);
            stmt.setLong(8, audit.getMaxContinuousDriving() != null ? audit.getMaxContinuousDriving().toMinutes() : 0);
            stmt.setString(9, audit.getAuditorName());
            stmt.setString(10, audit.getNotes());
            stmt.setObject(11, now);
            stmt.setObject(12, now);
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar auditoria de conformidade: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<ComplianceAudit> findById(int id) throws SQLException {
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToComplianceAudit(rs));
                }
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
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits";
        try (Connection conn = connectionFactory.getConnection(); 
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
    public boolean update(ComplianceAudit audit) throws SQLException {
        String sql = "UPDATE compliance_audits SET journey_id = ?, driver_id = ?, audit_date = ?, audit_timestamp = ?, status = ?, violations = ?, total_work_duration = ?, max_continuous_driving = ?, auditor_name = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setInt(2, audit.getDriverId());
            stmt.setObject(3, audit.getAuditDate());
            stmt.setObject(4, audit.getAuditTimestamp());
            stmt.setString(5, audit.getStatus().name());
            stmt.setString(6, audit.getViolations());
            stmt.setLong(7, audit.getTotalWorkDuration() != null ? audit.getTotalWorkDuration().toMinutes() : 0);
            stmt.setLong(8, audit.getMaxContinuousDriving() != null ? audit.getMaxContinuousDriving().toMinutes() : 0);
            stmt.setString(9, audit.getAuditorName());
            stmt.setString(10, audit.getNotes());
            stmt.setObject(11, LocalDateTime.now());
            stmt.setInt(12, audit.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar auditoria de conformidade: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (Connection conn = connectionFactory.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar auditoria de conformidade: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE journey_id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias por ID da jornada: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByDriverId(int driverId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE driver_id = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias por ID do motorista: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE audit_date BETWEEN ? AND ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, startDate);
            stmt.setObject(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias por intervalo de datas: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByStatus(ComplianceStatus status) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE status = ?";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias por status: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
        Duration totalWorkDuration = Duration.ofMinutes(rs.getLong("total_work_duration"));
        Duration maxContinuousDriving = Duration.ofMinutes(rs.getLong("max_continuous_driving"));

        return new ComplianceAudit(
                rs.getInt("id"),
                rs.getInt("journey_id"),
                rs.getInt("driver_id"),
                rs.getObject("audit_date", LocalDate.class),
                rs.getTimestamp("audit_timestamp").toLocalDateTime(),
                ComplianceStatus.valueOf(rs.getString("status")),
                rs.getString("violations"),
                totalWorkDuration,
                maxContinuousDriving,
                rs.getString("auditor_name"),
                rs.getString("notes"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
