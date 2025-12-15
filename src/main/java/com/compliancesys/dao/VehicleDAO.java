// src/main/java/com/compliancesys/dao/VehicleDAO.java (Interface)
package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Vehicle;

public interface VehicleDAO {
    int create(Vehicle vehicle) throws SQLException;
    Optional<Vehicle> findById(int id) throws SQLException;
    List<Vehicle> findAll() throws SQLException;
    boolean update(Vehicle vehicle) throws SQLException;
    boolean delete(int id) throws SQLException;

    // Métodos adicionados/confirmados com base nas necessidades do sistema
    Optional<Vehicle> findByPlate(String plate) throws SQLException;
    List<Vehicle> findByCompanyId(int companyId) throws SQLException; // Necessário pelo log
    List<Vehicle> findByModel(String model) throws SQLException; // Necessário pelo log
    List<Vehicle> findByYear(int year) throws SQLException; // Necessário pelo log
}
