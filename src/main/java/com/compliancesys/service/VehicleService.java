package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;

public interface VehicleService {
    Vehicle createVehicle(Vehicle vehicle) throws BusinessException, SQLException;
    Vehicle updateVehicle(Vehicle vehicle) throws BusinessException, SQLException;
    boolean deleteVehicle(int id) throws BusinessException, SQLException;
    Optional<Vehicle> getVehicleById(int id) throws SQLException;
    Optional<Vehicle> getVehicleByPlate(String plate) throws SQLException;
    List<Vehicle> getAllVehicles() throws SQLException;
    List<Vehicle> getVehiclesByCompanyId(int companyId) throws SQLException;
    List<Vehicle> getVehiclesByModel(String model) throws SQLException;
    List<Vehicle> getVehiclesByYear(int year) throws SQLException;
}
