package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Company;

/**
 * Interface para o Data Access Object (DAO) de Company.
 * Define as operações CRUD e de busca específicas para empresas.
 */
public interface CompanyDAO {

    /**
     * Insere uma nova empresa no banco de dados.
     * @param company O objeto Company a ser inserido.
     * @return O ID gerado para a nova empresa. // CORRIGIDO: Retorna int
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(Company company) throws SQLException; // CORRIGIDO: Retorna int

    /**
     * Busca uma empresa pelo seu ID.
     * @param id O ID da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> findById(int id) throws SQLException;

    /**
     * Busca uma empresa pelo seu CNPJ.
     * @param cnpj O CNPJ da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> findByCnpj(String cnpj) throws SQLException;

    /**
     * Busca todas as empresas.
     * @return Uma lista de todas as empresas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Company> findAll() throws SQLException;

    /**
     * Atualiza uma empresa existente no banco de dados.
     * @param company O objeto Company com os dados atualizados.
     * @return true se a empresa foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Company company) throws SQLException;

    /**
     * Deleta uma empresa pelo seu ID.
     * @param id O ID da empresa a ser deletada.
     * @return true se a empresa foi deletada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
