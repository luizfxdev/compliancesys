package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Journey;

public interface JourneyDAO {
    int create(Journey journey) throws SQLException;
    Optional<Journey> findById(int id) throws SQLException;
    List<Journey> findAll() throws SQLException;
    boolean update(Journey journey) throws SQLException;
    boolean delete(int id) throws SQLException;

    // Métodos adicionados/verificados para corresponder aos erros
    Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException;
    List<Journey> findByDriverId(int driverId) throws SQLException;
    List<Journey> findByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException;
    List<Journey> findByVehicleId(int vehicleId) throws SQLException;
    List<Journey> findByVehicleIdAndDate(int vehicleId, LocalDate journeyDate) throws SQLException;
    List<Journey> findByStatus(String status) throws SQLException;
    List<Journey> findByStatusAndDateRange(String status, LocalDate startDate, LocalDate endDate) throws SQLException;
}
