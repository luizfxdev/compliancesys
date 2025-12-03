package com.compliancesys.service;

import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de serviço de conformidade.
 * Define as operações de negócio para gerenciar auditorias e relatórios de conformidade.
 */
public interface ComplianceService {

    /**
     * Realiza uma auditoria de conformidade para uma jornada específica.
     * @param journeyId ID da jornada a ser auditada.
     * @return O ID do registro de auditoria criado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se a jornada não for encontrada ou for inválida.
     */
    int performComplianceAudit(int journeyId) throws SQLException, IllegalArgumentException;

    /**
     * Busca um registro de auditoria de conformidade pelo seu ID.
     * @param auditId ID da auditoria.
     * @return Um Optional contendo o ComplianceAudit se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<ComplianceAudit> getComplianceAuditById(int auditId) throws SQLException;

    /**
     * Retorna uma lista de todos os registros de auditoria de conformidade.
     * @return Uma lista de todos os registros de auditoria.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> getAllComplianceAudits() throws SQLException;

    /**
     * Busca registros de auditoria de conformidade por ID de jornada.
     * @param journeyId ID da jornada.
     * @return Uma lista de registros de auditoria para a jornada especificada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> getComplianceAuditsByJourneyId(int journeyId) throws SQLException;

    /**
     * Gera um relatório de conformidade para um motorista em um período específico.
     * @param driverId ID do motorista.
     * @param startDate Data de início do período.
     * @param endDate Data de fim do período.
     * @return Uma lista de ComplianceAudit que detalha a conformidade do motorista.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Gera um relatório de conformidade geral para todas as jornadas em um período.
     * @param startDate Data de início do período.
     * @param endDate Data de fim do período.
     * @return Uma lista de ComplianceAudit que detalha a conformidade geral.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> generateOverallComplianceReport(LocalDate startDate, LocalDate endDate) throws SQLException;
}
