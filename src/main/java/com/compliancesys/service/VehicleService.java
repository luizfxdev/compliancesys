// src/main/java/com/compliancesys/service/VehicleService.java
package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;

public interface VehicleService {
    Vehicle registerVehicle(Vehicle vehicle) throws BusinessException, SQLException;
    Optional<Vehicle> getVehicleById(int id) throws BusinessException, SQLException;
    Optional<Vehicle> getVehicleByPlate(String plate) throws BusinessException, SQLException;
    List<Vehicle> getAllVehicles() throws BusinessException, SQLException;
    boolean updateVehicle(Vehicle vehicle) throws BusinessException, SQLException;
    boolean deleteVehicle(int id) throws BusinessException, SQLException;
    List<Vehicle> getVehiclesByCompanyId(int companyId) throws BusinessException, SQLException; // Adicionado
    List<Vehicle> getVehiclesByModel(String model) throws BusinessException, SQLException; // Adicionado
    List<Vehicle> getVehiclesByYear(int year) throws BusinessException, SQLException; // Adicionado
}
