package com.compliancesys.service.impl;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.util.Validator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TimeRecordServiceImpl implements TimeRecordService {

    private final TimeRecordDAO timeRecordDAO;
    private final DriverDAO driverDAO;
    private final JourneyDAO journeyDAO;
    private final Validator validator;

    public TimeRecordServiceImpl(TimeRecordDAO timeRecordDAO, DriverDAO driverDAO, JourneyDAO journeyDAO, Validator validator) {
        this.timeRecordDAO = timeRecordDAO;
        this.driverDAO = driverDAO;
        this.journeyDAO = journeyDAO;
        this.validator = validator;
    }

    @Override
    public TimeRecord createTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException {
        validator.validate(timeRecord);

        Optional<Driver> existingDriver = driverDAO.findById(timeRecord.getDriverId());
        if (!existingDriver.isPresent()) {
            throw new BusinessException("Motorista com ID " + timeRecord.getDriverId() + " não encontrado.");
        }

        Optional<Journey> existingJourney = journeyDAO.findById(timeRecord.getJourneyId());
        if (!existingJourney.isPresent()) {
            throw new BusinessException("Jornada com ID " + timeRecord.getJourneyId() + " não encontrada.");
        }

        int id = timeRecordDAO.create(timeRecord);
        timeRecord.setId(id);
        return timeRecord;
    }

    @Override
    public TimeRecord updateTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException {
        validator.validate(timeRecord);

        Optional<TimeRecord> existingRecord = timeRecordDAO.findById(timeRecord.getId());
        if (!existingRecord.isPresent()) {
            throw new BusinessException("Registro de tempo com ID " + timeRecord.getId() + " não encontrado para atualização.");
        }

        Optional<Driver> existingDriver = driverDAO.findById(timeRecord.getDriverId());
        if (!existingDriver.isPresent()) {
            throw new BusinessException("Motorista com ID " + timeRecord.getDriverId() + " não encontrado.");
        }

        Optional<Journey> existingJourney = journeyDAO.findById(timeRecord.getJourneyId());
        if (!existingJourney.isPresent()) {
            throw new BusinessException("Jornada com ID " + timeRecord.getJourneyId() + " não encontrada.");
        }

        if (!timeRecordDAO.update(timeRecord)) {
            throw new SQLException("Falha ao atualizar registro de tempo com ID: " + timeRecord.getId());
        }
        return timeRecord;
    }

    @Override
    public boolean deleteTimeRecord(int id) throws BusinessException, SQLException {
        Optional<TimeRecord> existingRecord = timeRecordDAO.findById(id);
        if (!existingRecord.isPresent()) {
            throw new BusinessException("Registro de tempo com ID " + id + " não encontrado para exclusão.");
        }
        return timeRecordDAO.delete(id);
    }

    @Override
    public Optional<TimeRecord> getTimeRecordById(int id) throws SQLException {
        return timeRecordDAO.findById(id);
    }

    @Override
    public List<TimeRecord> getAllTimeRecords() throws SQLException {
        return timeRecordDAO.findAll();
    }

    @Override
    public List<TimeRecord> getTimeRecordsByJourneyId(int journeyId) throws BusinessException, SQLException {
        Optional<Journey> existingJourney = journeyDAO.findById(journeyId);
        if (!existingJourney.isPresent()) {
            throw new BusinessException("Jornada com ID " + journeyId + " não encontrada.");
        }
        return timeRecordDAO.findByJourneyId(journeyId);
    }

    @Override
    public List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException, SQLException {
        Optional<Driver> existingDriver = driverDAO.findById(driverId);
        if (!existingDriver.isPresent()) {
            throw new BusinessException("Motorista com ID " + driverId + " não encontrado.");
        }
        return timeRecordDAO.findByDriverId(driverId);
    }

    @Override
    public List<TimeRecord> getTimeRecordsByEventType(EventType eventType) throws SQLException {
        return timeRecordDAO.findByEventType(eventType);
    }

    @Override
    public List<TimeRecord> getTimeRecordsByRecordTimeRange(LocalDateTime start, LocalDateTime end) throws SQLException {
        return timeRecordDAO.findByRecordTimeRange(start, end);
    }

    @Override
    public Optional<TimeRecord> getLatestTimeRecordByDriverAndJourney(int driverId, int journeyId) throws BusinessException, SQLException {
        Optional<Driver> existingDriver = driverDAO.findById(driverId);
        if (!existingDriver.isPresent()) {
            throw new BusinessException("Motorista com ID " + driverId + " não encontrado.");
        }

        Optional<Journey> existingJourney = journeyDAO.findById(journeyId);
        if (!existingJourney.isPresent()) {
            throw new BusinessException("Jornada com ID " + journeyId + " não encontrada.");
        }
        return timeRecordDAO.findLatestByDriverIdAndJourneyId(driverId, journeyId);
    }
}
