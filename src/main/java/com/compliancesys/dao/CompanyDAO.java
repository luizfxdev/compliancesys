package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Company;

public interface CompanyDAO {
    int create(Company company) throws SQLException;
    Optional<Company> findById(int id) throws SQLException;
    Optional<Company> findByCnpj(String cnpj) throws SQLException;
    Optional<Company> findByLegalName(String legalName) throws SQLException;
    Optional<Company> findByTradingName(String tradingName) throws SQLException;
    List<Company> findAll() throws SQLException;
    boolean update(Company company) throws SQLException;
    boolean delete(int id) throws SQLException;
}
