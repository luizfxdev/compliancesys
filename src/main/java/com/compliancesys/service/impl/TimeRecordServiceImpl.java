package com.compliancesys.service.impl;

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeRecordServiceImpl implements TimeRecordService {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordServiceImpl.class.getName());

    private final TimeRecordDAO timeRecordDAO;
    private final Validator validator;

    public TimeRecordServiceImpl(TimeRecordDAO timeRecordDAO, Validator validator) {
        this.timeRecordDAO = timeRecordDAO;
        this.validator = validator;
    }

    @Override
    public TimeRecord createTimeRecord(TimeRecord timeRecord) throws BusinessException {
        if (timeRecord == null) {
            throw new BusinessException("Registro de ponto não pode ser nulo.");
        }
        if (timeRecord.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecord.getVehicleId() <= 0) {
            throw new BusinessException("ID do veículo inválido.");
        }
        if (timeRecord.getRecordTime() == null) {
            throw new BusinessException("Data/hora do registro não pode ser nula.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento não pode ser nulo.");
        }
        if (!validator.isValidLocation(timeRecord.getLocation())) {
            throw new BusinessException("Localização inválida.");
        }

        try {
            timeRecord.setCreatedAt(LocalDateTime.now());
            timeRecord.setUpdatedAt(LocalDateTime.now());
            int id = timeRecordDAO.create(timeRecord);
            timeRecord.setId(id);
            LOGGER.log(Level.INFO, "Registro de ponto criado com sucesso: ID {0}", timeRecord.getId());
            return timeRecord;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<TimeRecord> getTimeRecordById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do registro de ponto deve ser um valor positivo.");
        }
        try {
            return timeRecordDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de ponto por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getAllTimeRecords() throws BusinessException {
        try {
            return timeRecordDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os registros de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar todos os registros de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (date == null) {
            throw new BusinessException("Data não pode ser nula.");
        }
        try {
            return timeRecordDAO.findByDriverIdAndDate(driverId, date);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de ponto por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registros de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        try {
            return timeRecordDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de ponto por motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registros de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public TimeRecord updateTimeRecord(TimeRecord timeRecord) throws BusinessException {
        if (timeRecord == null || timeRecord.getId() <= 0) {
            throw new BusinessException("Registro de ponto inválido para atualização.");
        }
        if (timeRecord.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecord.getVehicleId() <= 0) {
            throw new BusinessException("ID do veículo inválido.");
        }
        if (timeRecord.getRecordTime() == null) {
            throw new BusinessException("Data/hora do registro não pode ser nula.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento não pode ser nulo.");
        }
        if (!validator.isValidLocation(timeRecord.getLocation())) {
            throw new BusinessException("Localização inválida.");
        }

        try {
            Optional<TimeRecord> existingRecord = timeRecordDAO.findById(timeRecord.getId());
            if (existingRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + timeRecord.getId() + " não encontrado para atualização.");
            }

            timeRecord.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            timeRecord.setCreatedAt(existingRecord.get().getCreatedAt());

            boolean updated = timeRecordDAO.update(timeRecord);
            if (updated) {
                LOGGER.log(Level.INFO, "Registro de ponto atualizado com sucesso: ID {0}", timeRecord.getId());
                return timeRecord;
            } else {
                throw new BusinessException("Falha ao atualizar o registro de ponto. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteTimeRecord(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do registro de ponto deve ser um valor positivo para exclusão.");
        }
        try {
            Optional<TimeRecord> existingRecord = timeRecordDAO.findById(id);
            if (existingRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + id + " não encontrado para exclusão.");
            }

            boolean deleted = timeRecordDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Registro de ponto com ID {0} deletado com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar registro de ponto com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }
}
