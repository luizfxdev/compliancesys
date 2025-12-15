// src/main/java/com/compliancesys/dao/impl/ComplianceAuditDAOImpl.java
package com.compliancesys.dao.impl;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus; // Importar o enum
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {
    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditDAOImpl.class.getName());
    private final Connection connection;

    public ComplianceAuditDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int create(ComplianceAudit audit) throws SQLException {
        String sql = "INSERT INTO compliance_audits (journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, audit.getJourneyId());
            stmt.setInt(2, audit.getDriverId());
            stmt.setObject(3, audit.getAuditDate());
            stmt.setObject(4, audit.getAuditTimestamp());
            stmt.setString(5, audit.getStatus() != null ? audit.getStatus().name() : null); // Salva o nome do enum
            stmt.setString(6, audit.getViolations());
            stmt.setObject(7, audit.getTotalWorkDuration() != null ? audit.getTotalWorkDuration().getSeconds() : null); // Salva Duration como segundos
            stmt.setObject(8, audit.getMaxContinuousDriving() != null ? audit.getMaxContinuousDriving().getSeconds() : null); // Salva Duration como segundos
            stmt.setString(9, audit.getAuditorName());
            stmt.setString(10, audit.getNotes());
            stmt.setObject(11, now);
            stmt.setObject(12, now);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar auditoria de conformidade, nenhuma linha afetada");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Falha ao criar auditoria de conformidade, nenhum ID gerado");
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setInt(2, audit.getDriverId());
            stmt.setObject(3, audit.getAuditDate());
            stmt.setObject(4, audit.getAuditTimestamp());
            stmt.setString(5, audit.getStatus() != null ? audit.getStatus().name() : null);
            stmt.setString(6, audit.getViolations());
            stmt.setObject(7, audit.getTotalWorkDuration() != null ? audit.getTotalWorkDuration().getSeconds() : null);
            stmt.setObject(8, audit.getMaxContinuousDriving() != null ? audit.getMaxContinuousDriving().getSeconds() : null);
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias por Journey ID: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByDriverId(int driverId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE driver_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditorias por Driver ID: " + e.getMessage(), e);
            throw e;
        }
        return audits;
    }

    @Override
    public List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE audit_date BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public Optional<ComplianceAudit> findByJourneyIdAndAuditDate(int journeyId, LocalDate auditDate) throws SQLException {
        String sql = "SELECT id, journey_id, driver_id, audit_date, audit_timestamp, status, violations, total_work_duration, max_continuous_driving, auditor_name, notes, created_at, updated_at FROM compliance_audits WHERE journey_id = ? AND audit_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, journeyId);
            stmt.setObject(2, auditDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToComplianceAudit(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar auditoria por Journey ID e Data de Auditoria: " + e.getMessage(), e);
            throw e;
        }
        return Optional.empty();
    }

    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
        // Converte segundos para Duration
        Long totalWorkDurationSeconds = rs.getObject("total_work_duration", Long.class);
        Duration totalWorkDuration = totalWorkDurationSeconds != null ? Duration.ofSeconds(totalWorkDurationSeconds) : null;

        Long maxContinuousDrivingSeconds = rs.getObject("max_continuous_driving", Long.class);
        Duration maxContinuousDriving = maxContinuousDrivingSeconds != null ? Duration.ofSeconds(maxContinuousDrivingSeconds) : null;

        // Converte String para ComplianceStatus enum
        String statusString = rs.getString("status");
        ComplianceStatus status = statusString != null ? ComplianceStatus.valueOf(statusString) : null;

        return new ComplianceAudit(
                rs.getInt("id"),
                rs.getInt("journey_id"),
                rs.getInt("driver_id"),
                rs.getObject("audit_date", LocalDate.class),
                rs.getObject("audit_timestamp", LocalDateTime.class),
                status, // Usa o enum
                rs.getString("violations"),
                totalWorkDuration,
                maxContinuousDriving,
                rs.getString("auditor_name"),
                rs.getString("notes"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
