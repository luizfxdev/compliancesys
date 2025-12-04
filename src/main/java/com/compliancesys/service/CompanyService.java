package com.compliancesys.service;

import com.compliancesys.model.Company;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de serviço da entidade Company.
 * Define as operações de negócio para gerenciar empresas.
 */
public interface CompanyService {

    /**
     * Registra uma nova empresa no sistema.
     * Realiza validações de negócio antes de persistir a empresa.
     * @param company Objeto Company a ser registrado.
     * @return O ID da empresa registrada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados da empresa forem inválidos.
     */
    int registerCompany(Company company) throws SQLException, IllegalArgumentException;

    /**
     * Busca uma empresa pelo seu ID.
     * @param companyId ID da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> getCompanyById(int companyId) throws SQLException;

    /**
     * Busca uma empresa pelo seu CNPJ.
     * @param cnpj CNPJ da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> getCompanyByCnpj(String cnpj) throws SQLException;

    /**
     * Retorna uma lista de todas as empresas registradas.
     * @return Uma lista de todas as empresas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Company> getAllCompanies() throws SQLException;

    /**
     * Atualiza as informações de uma empresa existente.
     * Realiza validações de negócio antes de atualizar.
     * @param company Objeto Company com os dados atualizados.
     * @return true se a empresa foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados da empresa forem inválidos.
     */
    boolean updateCompany(Company company) throws SQLException, IllegalArgumentException;

    /**
     * Remove uma empresa do sistema pelo seu ID.
     * @param companyId ID da empresa a ser removida.
     * @return true se a empresa foi removida com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteCompany(int companyId) throws SQLException;
}
