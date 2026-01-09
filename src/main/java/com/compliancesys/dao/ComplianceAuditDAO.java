// src/main/java/com/compliancesys/dao/ComplianceAuditDAO.java (Interface)
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
    boolean update(ComplianceAudit audit) throws SQLException;
    boolean delete(int id) throws SQLException;

    // Métodos adicionados/confirmados com base nas necessidades do sistema
    List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException;
    List<ComplianceAudit> findByDriverId(int driverId) throws SQLException;
    List<ComplianceAudit> findByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    Optional<ComplianceAudit> findByJourneyIdAndAuditDate(int journeyId, LocalDate auditDate) throws SQLException;
}
