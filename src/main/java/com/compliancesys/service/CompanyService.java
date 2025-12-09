package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;

public interface CompanyService {

    /**
     * Registra uma nova empresa no sistema.
     * @param company O objeto Company a ser registrado.
     * @return O objeto Company registrado com o ID gerado.
     * @throws BusinessException Se houver uma regra de negócio violada (ex: CNPJ duplicado, dados inválidos).
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Company registerCompany(Company company) throws BusinessException, SQLException;

    /**
     * Busca uma empresa pelo seu ID.
     * @param id O ID da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws BusinessException Se o ID for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> getCompanyById(int id) throws BusinessException, SQLException;

    /**
     * Busca uma empresa pelo seu CNPJ.
     * @param cnpj O CNPJ da empresa.
     * @return Um Optional contendo a Company se encontrada, ou um Optional vazio.
     * @throws BusinessException Se o CNPJ for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException, SQLException; // ADICIONADO

    /**
     * Busca todas as empresas.
     * @return Uma lista de todas as empresas.
     * @throws BusinessException Se ocorrer um erro de negócio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Company> getAllCompanies() throws BusinessException, SQLException;

    /**
     * Atualiza os dados de uma empresa existente.
     * @param company O objeto Company com os dados atualizados.
     * @return O objeto Company atualizado.
     * @throws BusinessException Se houver uma regra de negócio violada ou a empresa não for encontrada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Company updateCompany(Company company) throws BusinessException, SQLException;

    /**
     * Deleta uma empresa pelo seu ID.
     * @param companyId O ID da empresa a ser deletada.
     * @return true se a empresa foi deletada com sucesso, false caso contrário.
     * @throws BusinessException Se o ID for inválido ou a empresa não for encontrada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteCompany(int companyId) throws BusinessException, SQLException;
}
