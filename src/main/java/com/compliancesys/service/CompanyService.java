package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;

public interface CompanyService {
    Company createCompany(Company company) throws SQLException, BusinessException;
    Company updateCompany(Company company) throws SQLException, BusinessException;
    boolean deleteCompany(int id) throws SQLException, BusinessException;
    Optional<Company> getCompanyById(int id) throws SQLException;
    List<Company> getAllCompanies() throws SQLException;
    Optional<Company> getCompanyByCnpj(String cnpj) throws SQLException;
    Optional<Company> getCompanyByLegalName(String legalName) throws SQLException;
    Optional<Company> getCompanyByTradingName(String tradingName) throws SQLException;
}
