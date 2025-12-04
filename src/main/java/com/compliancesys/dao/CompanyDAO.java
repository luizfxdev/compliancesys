package com.compliancesys.dao;

import com.compliancesys.model.Company;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de acesso a dados (DAO) da entidade Company.
 * Define as operações CRUD e de busca específicas para empresas.
 */
public interface CompanyDAO {

    /**
     * Cria uma nova empresa no banco de dados.
     * @param company Objeto Company a ser criado.
     * @return O ID da empresa criada, ou -1 se a criação falhar.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(Company company) throws SQLException;

    /**
     * Busca uma empresa pelo seu ID.
     * @param id ID da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> findById(int id) throws SQLException;

    /**
     * Busca uma empresa pelo seu CNPJ.
     * @param cnpj CNPJ da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> findByCnpj(String cnpj) throws SQLException;

    /**
     * Retorna uma lista de todas as empresas registradas.
     * @return Uma lista de todas as empresas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Company> findAll() throws SQLException;

    /**
     * Atualiza as informações de uma empresa existente.
     * @param company Objeto Company com os dados atualizados.
     * @return true se a empresa foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Company company) throws SQLException;

    /**
     * Remove uma empresa do banco de dados pelo seu ID.
     * @param id ID da empresa a ser removida.
     * @return true se a empresa foi removida com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
