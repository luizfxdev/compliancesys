package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Driver;

public interface DriverDAO {
    int create(Driver driver) throws SQLException;
    Optional<Driver> findById(int id) throws SQLException;
    List<Driver> findAll() throws SQLException;
    boolean update(Driver driver) throws SQLException;
    boolean delete(int id) throws SQLException;

    Optional<Driver> findByCpf(String cpf) throws SQLException;
    Optional<Driver> findByLicenseNumber(String licenseNumber) throws SQLException;
    List<Driver> findByCompanyId(int companyId) throws SQLException;
    Optional<Driver> findByEmail(String email) throws SQLException;
}
