// src/main/java/com/compliancesys/service/DriverService.java
package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;

public interface DriverService {
    Driver registerDriver(Driver driver) throws BusinessException, SQLException;
    Optional<Driver> getDriverById(int driverId) throws BusinessException, SQLException;
    Optional<Driver> getDriverByCpf(String cpf) throws BusinessException, SQLException;
    Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws BusinessException, SQLException;
    List<Driver> getDriversByCompanyId(int companyId) throws BusinessException, SQLException;
    Optional<Driver> getDriverByEmail(String email) throws BusinessException, SQLException; // Adicionado
    List<Driver> getAllDrivers() throws BusinessException, SQLException;
    Driver updateDriver(Driver driver) throws BusinessException, SQLException;
    boolean deleteDriver(int driverId) throws BusinessException, SQLException;
}
