package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.util.Validator;

public class TimeRecordServiceImpl implements TimeRecordService {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordServiceImpl.class.getName());

    private final TimeRecordDAO timeRecordDAO;
    private final Validator validator;

    public TimeRecordServiceImpl(TimeRecordDAO timeRecordDAO, Validator validator) {
        this.timeRecordDAO = timeRecordDAO;
        this.validator = validator;
    }

    @Override
    public TimeRecord registerTimeRecord(TimeRecord timeRecord) throws BusinessException {
        if (timeRecord == null) {
            throw new BusinessException("Registro de ponto não pode ser nulo.");
        }
        if (timeRecord.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecord.getCompanyId() <= 0) { // AGORA EXISTE EM TimeRecord
            throw new BusinessException("ID da empresa inválido.");
        }
        if (timeRecord.getRecordTime() == null) { // USANDO getRecordTime()
            throw new BusinessException("Timestamp do registro de ponto é obrigatório.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento do registro de ponto é obrigatório.");
        }
        // Validações adicionais com o validator
        if (!validator.isValidDateTime(timeRecord.getRecordTime())) { // USANDO isValidDateTime e getRecordTime()
            throw new BusinessException("Timestamp do registro de ponto inválido ou no futuro.");
        }

        try {
            // Verifica se já existe um registro de ponto para o mesmo motorista, recordTime e tipo de evento
            Optional<TimeRecord> existingRecord = timeRecordDAO.findByDriverIdAndRecordTimeAndEventType( // MÉTODO CORRIGIDO
                    timeRecord.getDriverId(), timeRecord.getRecordTime(), timeRecord.getEventType());
            if (existingRecord.isPresent()) {
                throw new BusinessException("Já existe um registro de ponto com o mesmo motorista, timestamp e tipo de evento.");
            }

            timeRecord.setCreatedAt(LocalDateTime.now());
            timeRecord.setUpdatedAt(LocalDateTime.now());
            int id = timeRecordDAO.create(timeRecord);
            timeRecord.setId(id);
            LOGGER.log(Level.INFO, "Registro de ponto criado com sucesso: ID {0}", id);
            return timeRecord;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar registro de ponto.", e);
        }
    }

    @Override
    public Optional<TimeRecord> getTimeRecordById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID do registro de ponto inválido.");
        }
        try {
            return timeRecordDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de ponto por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registro de ponto.", e);
        }
    }

    @Override
    public List<TimeRecord> getAllTimeRecords() throws BusinessException {
        try {
            return timeRecordDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os registros de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registros de ponto.", e);
        }
    }

    @Override
    public boolean updateTimeRecord(TimeRecord timeRecord) throws BusinessException {
        if (timeRecord == null || timeRecord.getId() <= 0) {
            throw new BusinessException("Registro de ponto ou ID inválido para atualização.");
        }
        if (timeRecord.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecord.getCompanyId() <= 0) { // AGORA EXISTE EM TimeRecord
            throw new BusinessException("ID da empresa inválido.");
        }
        if (timeRecord.getRecordTime() == null) { // USANDO getRecordTime()
            throw new BusinessException("Timestamp do registro de ponto é obrigatório.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento do registro de ponto é obrigatório.");
        }
        // Validações adicionais com o validator
        if (!validator.isValidDateTime(timeRecord.getRecordTime())) { // USANDO isValidDateTime e getRecordTime()
            throw new BusinessException("Timestamp do registro de ponto inválido ou no futuro.");
        }

        try {
            Optional<TimeRecord> existingRecord = timeRecordDAO.findById(timeRecord.getId());
            if (existingRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + timeRecord.getId() + " não encontrado para atualização.");
            }

            // Verifica se a atualização resultaria em um registro duplicado (se recordTime e eventType mudarem)
            if (!existingRecord.get().getRecordTime().equals(timeRecord.getRecordTime()) || // USANDO getRecordTime()
                !existingRecord.get().getEventType().equals(timeRecord.getEventType())) {
                Optional<TimeRecord> potentialDuplicate = timeRecordDAO.findByDriverIdAndRecordTimeAndEventType( // MÉTODO CORRIGIDO
                        timeRecord.getDriverId(), timeRecord.getRecordTime(), timeRecord.getEventType());
                if (potentialDuplicate.isPresent() && potentialDuplicate.get().getId() != timeRecord.getId()) {
                    throw new BusinessException("Já existe outro registro de ponto com o mesmo motorista, timestamp e tipo de evento.");
                }
            }

            timeRecord.setUpdatedAt(LocalDateTime.now());
            boolean updated = timeRecordDAO.update(timeRecord);
            if (updated) {
                LOGGER.log(Level.INFO, "Registro de ponto atualizado com sucesso: ID {0}", timeRecord.getId());
            } else {
                LOGGER.log(Level.WARNING, "Falha ao atualizar registro de ponto. Nenhuma linha afetada.");
            }
            return updated;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar registro de ponto.", e);
        }
    }

    @Override
    public boolean deleteTimeRecord(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID do registro de ponto inválido para exclusão.");
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
            throw new BusinessException("Erro interno ao deletar registro de ponto.", e);
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
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de ponto por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registros de ponto.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (date == null) {
            throw new BusinessException("Data é obrigatória.");
        }
        try {
            return timeRecordDAO.findByDriverIdAndDate(driverId, date);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de ponto por ID do motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registros de ponto.", e);
        }
    }
}
