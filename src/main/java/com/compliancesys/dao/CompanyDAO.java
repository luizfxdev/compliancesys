// src/main/java/com/compliancesys/dao/CompanyDAO.java (Revisão, se necessário)
package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Company;

public interface CompanyDAO {
    int create(Company company) throws SQLException;
    Optional<Company> findById(int id) throws SQLException;
    Optional<Company> findByCnpj(String cnpj) throws SQLException;
    Optional<Company> findByLegalName(String legalName) throws SQLException; // Já adicionado
    Optional<Company> findByTradingName(String tradingName) throws SQLException; // Já adicionado
    Optional<Company> findByEmail(String email) throws SQLException; // Já adicionado
    Optional<Company> findByPhone(String phone) throws SQLException; // ADICIONADO
    List<Company> findAll() throws SQLException;
    boolean update(Company company) throws SQLException;
    boolean delete(int id) throws SQLException;
}
