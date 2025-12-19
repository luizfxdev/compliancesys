package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;

public interface ComplianceAuditService {
    ComplianceAudit createAudit(ComplianceAudit audit) throws SQLException, BusinessException;
    boolean updateAudit(ComplianceAudit audit) throws SQLException, BusinessException;
    boolean deleteAudit(int id) throws SQLException, BusinessException;
    Optional<ComplianceAudit> getAuditById(int id) throws SQLException;
    List<ComplianceAudit> getAllAudits() throws SQLException;
    List<ComplianceAudit> getAuditsByJourneyId(int journeyId) throws SQLException;
    List<ComplianceAudit> getAuditsByDriverId(int driverId) throws SQLException;
    List<ComplianceAudit> getAuditsByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    List<ComplianceAudit> getAuditsByStatus(ComplianceStatus status) throws SQLException;
}
