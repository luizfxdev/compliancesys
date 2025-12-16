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

    List<Journey> findByDriverId(int driverId) throws SQLException;
    List<Journey> findByVehicleId(int vehicleId) throws SQLException;
    List<Journey> findByCompanyId(int companyId) throws SQLException;
    List<Journey> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException;
}
