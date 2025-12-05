package com.compliancesys.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;

public interface JourneyService {
    Journey createJourney(Journey journey) throws BusinessException;
    Optional<Journey> getJourneyById(int id) throws BusinessException;
    List<Journey> getAllJourneys() throws BusinessException;
    Journey updateJourney(Journey journey) throws BusinessException;
    boolean deleteJourney(int id) throws BusinessException;

    // NOVO MÉTODO: Busca uma jornada específica por motorista e data
    Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException;

    // NOVO MÉTODO: Busca todas as jornadas de um motorista
    List<Journey> getJourneysByDriverId(int driverId) throws BusinessException;

    // Calcula e audita uma jornada com base nos registros de ponto
    Journey calculateAndAuditJourney(int driverId, List<TimeRecord> timeRecords) throws BusinessException;
}
