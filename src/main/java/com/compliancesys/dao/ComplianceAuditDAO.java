package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.ComplianceAudit;

/**
 * Interface para o Data Access Object (DAO) de ComplianceAudit.
 * Define as operações CRUD e de busca específicas para auditorias de conformidade.
 */
public interface ComplianceAuditDAO {
    
    /**
     * Cria uma nova auditoria de conformidade no banco de dados.
     * @param audit O objeto ComplianceAudit a ser criado.
     * @return O ID gerado para a nova auditoria.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(ComplianceAudit audit) throws SQLException;

    /**
     * Busca uma auditoria de conformidade pelo seu ID.
     * @param id O ID da auditoria.
     * @return Um Optional contendo o ComplianceAudit se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<ComplianceAudit> findById(int id) throws SQLException;

    /**
     * Busca todas as auditorias de conformidade.
     * @return Uma lista de todas as auditorias.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> findAll() throws SQLException;

    /**
     * Busca auditorias de conformidade por ID de jornada.
     * @param journeyId O ID da jornada.
     * @return Uma lista de auditorias associadas à jornada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException;

    /**
     * Busca auditorias de conformidade por ID de motorista e período de datas.
     * @param driverId O ID do motorista.
     * @param startDate Data inicial do período.
     * @param endDate Data final do período.
     * @return Uma lista de auditorias do motorista no período especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> findByDriverIdAndDate(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Atualiza uma auditoria de conformidade existente no banco de dados.
     * @param audit O objeto ComplianceAudit com os dados atualizados.
     * @return true se a auditoria foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(ComplianceAudit audit) throws SQLException;

    /**
     * Deleta uma auditoria de conformidade pelo seu ID.
     * @param id O ID da auditoria a ser deletada.
     * @return true se a auditoria foi deletada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}