package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;

public interface DriverService {
    Driver registerDriver(Driver driver) throws SQLException, BusinessException;
    Driver updateDriver(Driver driver) throws SQLException, BusinessException;
    boolean deleteDriver(int id) throws SQLException, BusinessException;
    Optional<Driver> getDriverById(int id) throws SQLException;
    List<Driver> getAllDrivers() throws SQLException;
    Optional<Driver> getDriverByCpf(String cpf) throws SQLException;
    Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws SQLException;
    List<Driver> getDriversByCompanyId(int companyId) throws SQLException;
    Optional<Driver> getDriverByEmail(String email) throws SQLException;
}
