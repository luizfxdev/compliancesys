// src/main/java/com/compliancesys/service/JourneyService.java
package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;

public interface JourneyService {
    Journey createJourney(Journey journey) throws BusinessException, SQLException;
    Optional<Journey> getJourneyById(int journeyId) throws BusinessException, SQLException;
    List<Journey> getAllJourneys() throws BusinessException, SQLException;
    Journey updateJourney(Journey journey) throws BusinessException, SQLException;
    boolean deleteJourney(int journeyId) throws BusinessException, SQLException;
    List<Journey> getJourneysByDriverId(int driverId) throws BusinessException, SQLException; // Adicionado
    List<Journey> getJourneysByVehicleId(int vehicleId) throws BusinessException, SQLException; // Adicionado
    List<Journey> getJourneysByCompanyId(int companyId) throws BusinessException, SQLException; // Adicionado
    List<Journey> getJourneysByDateRange(LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException; // Adicionado
    Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException, SQLException; // Adicionado
}
