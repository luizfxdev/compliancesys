package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.ComplianceAudit;

public interface ComplianceAuditDAO {
    int create(ComplianceAudit audit) throws SQLException;
    Optional<ComplianceAudit> findById(int id) throws SQLException;
    List<ComplianceAudit> findAll() throws SQLException;
    boolean update(ComplianceAudit audit) throws SQLException; // Retorno boolean
    boolean delete(int id) throws SQLException;
    List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException;
    List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
}
