package com.compliancesys.service;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceReport; // Importar se for usar
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ComplianceService {
    // Cria um novo registro de auditoria de conformidade
    ComplianceAudit createComplianceAudit(ComplianceAudit audit) throws BusinessException;

    // Busca um registro de auditoria por ID
    Optional<ComplianceAudit> getComplianceAuditById(int id) throws BusinessException;

    // Busca todos os registros de auditoria
    List<ComplianceAudit> getAllComplianceAudits() throws BusinessException;

    // Busca registros de auditoria por ID da jornada
    List<ComplianceAudit> getComplianceAuditsByJourneyId(int journeyId) throws BusinessException; // NOVO MÉTODO

    // Atualiza um registro de auditoria de conformidade
    ComplianceAudit updateComplianceAudit(ComplianceAudit audit) throws BusinessException;

    // Deleta um registro de auditoria de conformidade
    boolean deleteComplianceAudit(int id) throws BusinessException;

    // Realiza uma auditoria de conformidade para uma jornada específica
    ComplianceAudit performComplianceAudit(int journeyId) throws BusinessException;

    // Gera um relatório de conformidade para um motorista em um período
    ComplianceReport generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException;

    // Gera um relatório de conformidade geral para um período
    List<ComplianceAudit> generateOverallComplianceReport(LocalDate startDate, LocalDate endDate) throws BusinessException; // Assinatura ajustada para retornar List<ComplianceAudit>
}
