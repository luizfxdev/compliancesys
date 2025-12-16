package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;

public interface ComplianceAuditDAO {
    ComplianceAudit create(ComplianceAudit audit) throws SQLException;
    Optional<ComplianceAudit> findById(int id) throws SQLException;
    List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException;
    List<ComplianceAudit> findByDriverId(int driverId) throws SQLException;
    List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    List<ComplianceAudit> findByStatus(ComplianceStatus status) throws SQLException;
    List<ComplianceAudit> findAll() throws SQLException;
    boolean update(ComplianceAudit audit) throws SQLException;
    boolean delete(int id) throws SQLException;
}
