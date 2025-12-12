package com.compliancesys.dao.impl;

<<<<<<< Updated upstream
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

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {

    private final Connection connection;

    public ComplianceAuditDAOImpl(Connection connection) {
        this.connection = connection;
=======
import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceStatus; // Importa o enum ComplianceStatus

import javax.sql.DataSource; // Importa DataSource
import java.sql.*;
import java.time.LocalDateTime; // Usar LocalDateTime para created_at e updated_at
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComplianceAuditDAOImpl implements ComplianceAuditDAO {

    private final DataSource dataSource; // Adiciona o DataSource

    // Construtor que recebe o DataSource
    public ComplianceAuditDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
>>>>>>> Stashed changes
    }

    @Override
    public int create(ComplianceAudit audit) throws SQLException {
<<<<<<< Updated upstream
        // SQL usa 'status' e 'details', que são os nomes das colunas no DB.
        String sql = "INSERT INTO compliance_audits (journey_id, audit_date, status, details, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setTimestamp(2, Timestamp.valueOf(audit.getAuditDate()));
            stmt.setString(3, audit.getComplianceStatus()); // CORRIGIDO: getStatus() -> getComplianceStatus()
            stmt.setString(4, audit.getNotes());            // CORRIGIDO: getDetails() -> getNotes()
            stmt.setTimestamp(5, Timestamp.valueOf(audit.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(audit.getUpdatedAt()));
=======
        String sql = "INSERT INTO compliance_audits (journey_id, audit_date, compliance_status, auditor_name, notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, audit.getJourneyId());
            stmt.setObject(2, audit.getAuditDate());
            stmt.setString(3, audit.getComplianceStatus().name()); // Salva o nome do enum
            stmt.setString(4, audit.getAuditorName());
            stmt.setString(5, audit.getNotes());
            stmt.setObject(6, now); // created_at
            stmt.setObject(7, now); // updated_at
>>>>>>> Stashed changes

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar auditoria, nenhum ID gerado.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
<<<<<<< Updated upstream
                    throw new SQLException("Falha ao criar auditoria de conformidade, nenhum ID gerado.");
=======
                    throw new SQLException("Falha ao criar auditoria, nenhum ID gerado.");
>>>>>>> Stashed changes
                }
            }
        }
    }

    @Override
    public Optional<ComplianceAudit> findById(int id) throws SQLException {
        String sql = "SELECT * FROM compliance_audits WHERE id = ?";
<<<<<<< Updated upstream
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
=======
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        String sql = "SELECT * FROM compliance_audits ORDER BY audit_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
=======
        String sql = "SELECT * FROM compliance_audits";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
>>>>>>> Stashed changes
            while (rs.next()) {
                audits.add(mapResultSetToComplianceAudit(rs));
            }
        }
        return audits;
    }

    @Override
    public boolean update(ComplianceAudit audit) throws SQLException {
        // SQL usa 'status' e 'details', que são os nomes das colunas no DB.
        String sql = "UPDATE compliance_audits SET journey_id = ?, audit_date = ?, status = ?, details = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setTimestamp(2, Timestamp.valueOf(audit.getAuditDate()));
            stmt.setString(3, audit.getComplianceStatus()); // CORRIGIDO: getStatus() -> getComplianceStatus()
            stmt.setString(4, audit.getNotes());            // CORRIGIDO: getDetails() -> getNotes()
            stmt.setTimestamp(5, Timestamp.valueOf(audit.getUpdatedAt()));
            stmt.setInt(6, audit.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
<<<<<<< Updated upstream
        String sql = "SELECT * FROM compliance_audits WHERE journey_id = ? ORDER BY audit_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
=======
        String sql = "SELECT * FROM compliance_audits WHERE journey_id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    public List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT * FROM compliance_audits WHERE audit_date BETWEEN ? AND ? ORDER BY audit_date ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    @Override
    public List<ComplianceAudit> findByDriverIdAndAuditDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ComplianceAudit> audits = new ArrayList<>();
        String sql = "SELECT ca.* FROM compliance_audits ca " +
                     "INNER JOIN journeys j ON ca.journey_id = j.id " +
                     "WHERE j.driver_id = ? AND ca.audit_date BETWEEN ? AND ? " +
                     "ORDER BY ca.audit_date ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setTimestamp(2, Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(3, Timestamp.valueOf(endDate.atTime(23, 59, 59)));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    audits.add(mapResultSetToComplianceAudit(rs));
                }
            }
=======
    public boolean update(ComplianceAudit audit) throws SQLException {
        String sql = "UPDATE compliance_audits SET journey_id = ?, audit_date = ?, compliance_status = ?, auditor_name = ?, notes = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, audit.getJourneyId());
            stmt.setObject(2, audit.getAuditDate());
            stmt.setString(3, audit.getComplianceStatus().name()); // Salva o nome do enum
            stmt.setString(4, audit.getAuditorName());
            stmt.setString(5, audit.getNotes());
            stmt.setObject(6, LocalDateTime.now()); // updated_at
            stmt.setInt(7, audit.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM compliance_audits WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); // Usa o DataSource
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
>>>>>>> Stashed changes
        }
        return audits;
    }

    private ComplianceAudit mapResultSetToComplianceAudit(ResultSet rs) throws SQLException {
<<<<<<< Updated upstream
        ComplianceAudit audit = new ComplianceAudit();
        audit.setId(rs.getInt("id"));
        audit.setJourneyId(rs.getInt("journey_id"));
        audit.setAuditDate(rs.getTimestamp("audit_date").toLocalDateTime());
        audit.setComplianceStatus(rs.getString("status")); // Lendo da coluna 'status' do DB
        audit.setNotes(rs.getString("details"));           // Lendo da coluna 'details' do DB
        audit.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        audit.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return audit;
=======
        return new ComplianceAudit(
                rs.getInt("id"),
                rs.getInt("journey_id"),
                rs.getObject("audit_date", LocalDateTime.class),
                ComplianceStatus.valueOf(rs.getString("compliance_status")), // Converte String para enum
                rs.getString("auditor_name"),
                rs.getString("notes"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
>>>>>>> Stashed changes
    }
}
