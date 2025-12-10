package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;

public interface CompanyService {

    Company registerCompany(Company company) throws BusinessException, SQLException;

    Optional<Company> getCompanyById(int id) throws BusinessException, SQLException;

    Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException, SQLException;

    Optional<Company> getCompanyByName(String name) throws BusinessException, SQLException;

    List<Company> getAllCompanies() throws BusinessException, SQLException;

    Company updateCompany(Company company) throws BusinessException, SQLException;

    boolean deleteCompany(int companyId) throws BusinessException, SQLException;
}
