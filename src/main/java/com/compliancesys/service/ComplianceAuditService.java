// src/main/java/com/compliancesys/service/ComplianceAuditService.java
package com.compliancesys.service;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ComplianceAuditService {
    ComplianceAudit performAudit(Journey journey, List<TimeRecord> timeRecords) throws BusinessException, SQLException;
    Optional<ComplianceAudit> getAuditById(int auditId) throws BusinessException, SQLException;
    List<ComplianceAudit> getAuditsByJourneyId(int journeyId) throws BusinessException, SQLException;
    List<ComplianceAudit> getAuditsByDriverId(int driverId) throws BusinessException, SQLException;
    List<ComplianceAudit> getAuditsByDateRange(LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException;
    List<ComplianceAudit> getAuditsByJourneyIdAndDate(int journeyId, LocalDate auditDate) throws BusinessException, SQLException;
    List<ComplianceAudit> getAllAudits() throws BusinessException, SQLException;
    boolean updateAudit(ComplianceAudit audit) throws BusinessException, SQLException;
    boolean deleteAudit(int auditId) throws BusinessException, SQLException;
}
