// src/main/java/com/compliancesys/service/CompanyService.java
package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;

public interface CompanyService {
    // Renomeado de registerCompany para createCompany para alinhar com o Servlet
    Company createCompany(Company company) throws BusinessException, SQLException;
    Optional<Company> getCompanyById(int companyId) throws BusinessException, SQLException;
    Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException, SQLException;
    List<Company> getAllCompanies() throws BusinessException, SQLException;
    Company updateCompany(Company company) throws BusinessException, SQLException;
    boolean deleteCompany(int companyId) throws BusinessException, SQLException;
    // Adicionar métodos que podem ser chamados por outros services ou controllers
    Optional<Company> getCompanyByLegalName(String legalName) throws BusinessException, SQLException;
    Optional<Company> getCompanyByTradingName(String tradingName) throws BusinessException, SQLException;
    Optional<Company> getCompanyByEmail(String email) throws BusinessException, SQLException;
    Optional<Company> getCompanyByPhone(String phone) throws BusinessException, SQLException;
}
