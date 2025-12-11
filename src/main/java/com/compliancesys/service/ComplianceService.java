package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.report.ComplianceReport;

/**
 * Interface para serviços relacionados à conformidade de jornadas de trabalho.
 * Define as operações de negócio para gerenciar e auditar a conformidade.
 */
public interface ComplianceService {

    /**
     * Realiza uma auditoria de conformidade para uma jornada específica.
     *
     * @param journeyId O ID da jornada a ser auditada.
     * @return O objeto ComplianceAudit resultante da auditoria.
     * @throws BusinessException Se houver uma regra de negócio violada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    ComplianceAudit auditJourney(int journeyId) throws BusinessException, SQLException;

    /**
     * Gera um relatório de conformidade para um motorista em um período específico.
     *
     * @param driverId O ID do motorista.
     * @param startDate A data de início do período do relatório.
     * @param endDate A data de fim do período do relatório.
     * @return Um objeto ComplianceReport contendo os detalhes da conformidade do motorista.
     * @throws BusinessException Se houver uma regra de negócio violada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    ComplianceReport generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException;

    /**
     * Busca auditorias de conformidade por ID do motorista e intervalo de datas.
     *
     * @param driverId O ID do motorista.
     * @param startDate A data de início do intervalo.
     * @param endDate A data de fim do intervalo.
     * @return Uma lista de ComplianceAudit que se enquadram nos critérios.
     * @throws BusinessException Se o ID do motorista for inválido ou datas forem nulas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> getAuditsByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException;
}
