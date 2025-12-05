package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.JourneyService;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class JourneyServiceImpl implements JourneyService {

    private static final Logger LOGGER = Logger.getLogger(JourneyServiceImpl.class.getName());
    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO;
    private final Validator validator;
    private final TimeUtil timeUtil;

    private static final Duration MAX_DRIVING_TIME_DAILY = Duration.ofHours(10);
    private static final Duration MIN_REST_TIME_DAILY = Duration.ofHours(11);

    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO, Validator validator, TimeUtil timeUtil) {
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    @Override
    public Journey createJourney(Journey journey) throws BusinessException {
        if (journey == null) {
            throw new BusinessException("Jornada não pode ser nula.");
        }
        if (journey.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada não pode ser nula.");
        }
        if (!validator.isValidLocation(journey.getStartLocation())) {
            throw new BusinessException("Local de início da jornada inválido.");
        }
        if (!validator.isValidLocation(journey.getEndLocation())) {
            throw new BusinessException("Local de fim da jornada inválido.");
        }

        try {
            Optional<Journey> existingJourney = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
            if (existingJourney.isPresent()) {
                throw new BusinessException("Já existe uma jornada registrada para o motorista " + journey.getDriverId() + " na data " + journey.getJourneyDate() + ".");
            }

            journey.setCreatedAt(LocalDateTime.now());
            journey.setUpdatedAt(LocalDateTime.now());
            int id = journeyDAO.create(journey);
            journey.setId(id);
            LOGGER.log(Level.INFO, "Jornada criada com sucesso: ID {0}", id);
            return journey;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da jornada inválido.");
        }
        try {
            return journeyDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getAllJourneys() throws BusinessException {
        try {
            return journeyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as jornadas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornadas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Journey updateJourney(Journey journey) throws BusinessException {
        if (journey == null || journey.getId() <= 0) {
            throw new BusinessException("Jornada ou ID inválido para atualização.");
        }
        if (journey.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada não pode ser nula.");
        }
        if (!validator.isValidLocation(journey.getStartLocation())) {
            throw new BusinessException("Local de início da jornada inválido.");
        }
        if (!validator.isValidLocation(journey.getEndLocation())) {
            throw new BusinessException("Local de fim da jornada inválido.");
        }

        try {
            Optional<Journey> existingJourney = journeyDAO.findById(journey.getId());
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + journey.getId() + " não encontrada.");
            }

            journey.setUpdatedAt(LocalDateTime.now());
            journey.setCreatedAt(existingJourney.get().getCreatedAt());

            boolean updated = journeyDAO.update(journey);
            if (updated) {
                LOGGER.log(Level.INFO, "Jornada atualizada com sucesso: ID {0}", journey.getId());
                return journey;
            } else {
                throw new BusinessException("Falha ao atualizar jornada. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteJourney(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo para exclusão.");
        }
        try {
            Optional<Journey> existingJourney = journeyDAO.findById(id);
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + id + " não encontrada para exclusão.");
            }

            boolean deleted = journeyDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Jornada com ID {0} deletada com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar jornada com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (journeyDate == null) {
            throw new BusinessException("Data da jornada não pode ser nula.");
        }
        try {
            return journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByDriverId(int driverId) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        try {
            return journeyDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornadas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Journey calculateAndAuditJourney(int driverId, List<TimeRecord> timeRecords) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecords == null || timeRecords.isEmpty()) {
            throw new BusinessException("Nenhum registro de ponto fornecido para calcular a jornada.");
        }

        timeRecords.sort(Comparator.comparing(TimeRecord::getRecordTime));

        LocalDate journeyDate = timeRecords.get(0).getRecordTime().toLocalDate();

        Duration totalDrivingTime = Duration.ZERO;
        Duration totalRestTime = Duration.ZERO;
        LocalDateTime lastEventTime = null;
        EventType lastEventType = null;

        for (TimeRecord record : timeRecords) {
            if (lastEventTime != null) {
                Duration durationBetweenEvents = Duration.between(lastEventTime, record.getRecordTime());

                if (lastEventType == EventType.START_DRIVING || lastEventType == EventType.RESUME_DRIVING) {
                    totalDrivingTime = totalDrivingTime.plus(durationBetweenEvents);
                } else if (lastEventType == EventType.START_REST || lastEventType == EventType.END_DRIVING) {
                    totalRestTime = totalRestTime.plus(durationBetweenEvents);
                }
            }
            lastEventTime = record.getRecordTime();
            lastEventType = record.getEventType();
        }

        ComplianceStatus complianceStatus = ComplianceStatus.COMPLIANT;
        boolean dailyLimitExceeded = false;

        if (timeUtil.isAboveMinDuration(totalDrivingTime, MAX_DRIVING_TIME_DAILY)) {
            complianceStatus = ComplianceStatus.NON_COMPLIANT;
            dailyLimitExceeded = true;
        }
        if (!timeUtil.isAboveMinDuration(totalRestTime, MIN_REST_TIME_DAILY)) {
            complianceStatus = ComplianceStatus.NON_COMPLIANT;
        }

        Optional<Journey> existingJourneyOptional;
        try {
            existingJourneyOptional = journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada existente para cálculo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao calcular jornada. Tente novamente mais tarde.", e);
        }

        Journey journey;
        if (existingJourneyOptional.isPresent()) {
            journey = existingJourneyOptional.get();
            journey.setTotalDrivingTime(totalDrivingTime);
            journey.setTotalRestTime(totalRestTime);
            journey.setStatus(complianceStatus);
            journey.setDailyLimitExceeded(dailyLimitExceeded);
            journey.setStartLocation(timeRecords.get(0).getLocation());
            journey.setEndLocation(timeRecords.get(timeRecords.size() - 1).getLocation());
            journey.setStartTime(timeRecords.get(0).getRecordTime());
            journey.setEndTime(timeRecords.get(timeRecords.size() - 1).getRecordTime());
            journey.setVehicleId(timeRecords.get(0).getVehicleId());
            updateJourney(journey);
            LOGGER.log(Level.INFO, "Jornada existente atualizada após cálculo: ID {0}", journey.getId());
        } else {
            journey = new Journey(
                    0,
                    driverId,
                    timeRecords.get(0).getVehicleId(),
                    journeyDate,
                    timeRecords.get(0).getRecordTime(),
                    timeRecords.get(timeRecords.size() - 1).getRecordTime(),
                    timeRecords.get(0).getLocation(),
                    timeRecords.get(timeRecords.size() - 1).getLocation(),
                    totalDrivingTime,
                    totalRestTime,
                    Duration.ZERO,
                    complianceStatus,
                    dailyLimitExceeded
            );
            journey = createJourney(journey);
            LOGGER.log(Level.INFO, "Nova jornada criada após cálculo: ID {0}", journey.getId());
        }

        return journey;
    }
}