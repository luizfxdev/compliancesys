package com.compliancesys.dao;

import com.compliancesys.model.ComplianceAudit; // Importa a classe ComplianceAudit.
import java.sql.SQLException;                   // Importa para lidar com exceções SQL.
import java.util.List;                          // Importa para usar List.
import java.util.Optional;                      // Importa para usar Optional.

/**
 * Interface para operações de acesso a dados da entidade ComplianceAudit.
 * Define os métodos CRUD e de busca para ComplianceAudit.
 */
public interface ComplianceAuditDAO {

    /**
     * Insere um novo registro de auditoria de conformidade no banco de dados.
     * @param audit Objeto ComplianceAudit a ser inserido.
     * @return O ID da auditoria inserida.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int insert(ComplianceAudit audit) throws SQLException;

    /**
     * Busca um registro de auditoria pelo seu ID.
     * @param id ID da auditoria.
     * @return Um Optional contendo o ComplianceAudit se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<ComplianceAudit> findById(int id) throws SQLException;

    /**
     * Retorna todos os registros de auditoria.
     * @return Uma lista de todos os registros de auditoria.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> findAll() throws SQLException;

    /**
     * Busca registros de auditoria por ID de jornada.
     * @param journeyId ID da jornada.
     * @return Uma lista de registros de auditoria para a jornada especificada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<ComplianceAudit> findByJourneyId(int journeyId) throws SQLException;

    /**
     * Atualiza os dados de um registro de auditoria existente.
     * @param audit Objeto ComplianceAudit com os dados atualizados.
     * @return true se o registro foi atualizado, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(ComplianceAudit audit) throws SQLException;

    /**
     * Deleta um registro de auditoria pelo seu ID.
     * @param id ID da auditoria a ser deletada.
     * @return true se a auditoria foi deletada, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
