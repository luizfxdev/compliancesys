// src/main/java/com/compliancesys/service/impl/TimeRecordServiceImpl.java
package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class TimeRecordServiceImpl implements TimeRecordService {
    private static final Logger LOGGER = Logger.getLogger(TimeRecordServiceImpl.class.getName());
    private final TimeRecordDAO timeRecordDAO;
    private final JourneyDAO journeyDAO;
    private final DriverDAO driverDAO;
    private final Validator validator;
    private final TimeUtil timeUtil;

    public TimeRecordServiceImpl(TimeRecordDAO timeRecordDAO, JourneyDAO journeyDAO,
                                 DriverDAO driverDAO, Validator validator, TimeUtil timeUtil) {
        this.timeRecordDAO = timeRecordDAO;
        this.journeyDAO = journeyDAO;
        this.driverDAO = driverDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    @Override
    public TimeRecord registerTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException {
        if (timeRecord == null) {
            throw new BusinessException("Dados do registro de tempo não podem ser nulos.");
        }
        if (!validator.isValidId(timeRecord.getJourneyId())) {
            throw new BusinessException("ID da jornada inválido.");
        }
        if (!validator.isValidId(timeRecord.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecord.getTimestamp() == null) {
            throw new BusinessException("Timestamp do registro é obrigatório.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento do registro é obrigatório.");
        }
        if (timeRecord.getLatitude() == null || !validator.isValidLatitude(timeRecord.getLatitude())) {
            throw new BusinessException("Latitude inválida.");
        }
        if (timeRecord.getLongitude() == null || !validator.isValidLongitude(timeRecord.getLongitude())) {
            throw new BusinessException("Longitude inválida.");
        }

        try {
            // Validar se a jornada e o motorista existem
            Optional<Journey> journey = journeyDAO.findById(timeRecord.getJourneyId());
            if (!journey.isPresent()) {
                throw new BusinessException("Jornada com ID " + timeRecord.getJourneyId() + " não encontrada.");
            }
            if (!driverDAO.findById(timeRecord.getDriverId()).isPresent()) {
                throw new BusinessException("Motorista com ID " + timeRecord.getDriverId() + " não encontrado.");
            }

            // Lógica de validação de sequência de eventos
            List<TimeRecord> existingRecords = timeRecordDAO.findByJourneyIdOrderedByTimestamp(timeRecord.getJourneyId());
            TimeRecord lastRecord = existingRecords.isEmpty() ? null : existingRecords.get(existingRecords.size() - 1); // Último registro (mais recente)

            if (lastRecord != null) {
                // Timestamp deve ser posterior ao último registro
                if (timeRecord.getTimestamp().isBefore(lastRecord.getTimestamp())) {
                    throw new BusinessException("Timestamp do novo registro (" + timeRecord.getTimestamp() + ") deve ser posterior ao último registro (" + lastRecord.getTimestamp() + ").");
                }

                // Validação de transições de estado
                switch (lastRecord.getEventType()) {
                    case START_DRIVE:
                        if (timeRecord.getEventType() == EventType.START_DRIVE) {
                            throw new BusinessException("Não é possível iniciar uma nova direção sem finalizar a anterior.");
                        }
                        break;
                    case END_DRIVE:
                        if (timeRecord.getEventType() == EventType.END_DRIVE) {
                            throw new BusinessException("Não é possível finalizar uma direção que já está finalizada.");
                        }
                        break;
                    case START_REST:
                        if (timeRecord.getEventType() == EventType.START_REST) {
                            throw new BusinessException("Não é possível iniciar um novo descanso sem finalizar o anterior.");
                        }
                        break;
                    case END_REST: // CORRIGIDO: Removido EventType.
                        if (timeRecord.getEventType() == EventType.END_REST) {
                            throw new BusinessException("Não é possível finalizar um descanso que já está finalizado.");
                        }
                        break;
                    case START_WORK:
                        if (timeRecord.getEventType() == EventType.START_WORK) {
                            throw new BusinessException("Não é possível iniciar um novo trabalho sem finalizar o anterior.");
                        }
                        break;
                    case END_WORK: // CORRIGIDO: Removido EventType.
                        if (timeRecord.getEventType() == EventType.END_WORK) {
                            throw new BusinessException("Não é possível finalizar um trabalho que já está finalizado.");
                        }
                        break;
                    case START_WAITING:
                        if (timeRecord.getEventType() == EventType.START_WAITING) {
                            throw new BusinessException("Não é possível iniciar uma nova espera sem finalizar a anterior.");
                        }
                        break;
                    case END_WAITING: // CORRIGIDO: Removido EventType.
                        if (timeRecord.getEventType() == EventType.END_WAITING) {
                            throw new BusinessException("Não é possível finalizar uma espera que já está finalizada.");
                        }
                        break;
                    // Adicione mais casos conforme necessário para outros EventTypes
                    default:
                        // Lógica padrão ou nenhuma restrição específica
                        break;
                }
            } else {
                // Se não há registros anteriores, o primeiro deve ser um evento de início
                if (timeRecord.getEventType() != EventType.START_DRIVE &&
                    timeRecord.getEventType() != EventType.START_WORK &&
                    timeRecord.getEventType() != EventType.START_REST &&
                    timeRecord.getEventType() != EventType.START_WAITING) {
                    throw new BusinessException("O primeiro registro de tempo para uma jornada deve ser um evento de início (START_DRIVE, START_WORK, START_REST ou START_WAITING).");
                }
            }

            timeRecord.setCreatedAt(LocalDateTime.now());
            timeRecord.setUpdatedAt(LocalDateTime.now());
            int id = timeRecordDAO.create(timeRecord);
            if (id <= 0) {
                throw new BusinessException("Falha ao registrar tempo. Tente novamente.");
            }
            timeRecord.setId(id);
            LOGGER.log(Level.INFO, "Registro de tempo criado com sucesso: ID {0} para jornada {1}, evento {2}",
                    new Object[]{timeRecord.getId(), timeRecord.getJourneyId(), timeRecord.getEventType().name()});
            return timeRecord;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar tempo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar tempo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<TimeRecord> getTimeRecordById(int recordId) throws BusinessException, SQLException {
        if (!validator.isValidId(recordId)) {
            throw new BusinessException("ID do registro de tempo inválido.");
        }
        try {
            return timeRecordDAO.findById(recordId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de tempo por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registro de tempo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getAllTimeRecords() throws BusinessException, SQLException {
        try {
            return timeRecordDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os registros de tempo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registros de tempo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public TimeRecord updateTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException {
        if (timeRecord == null || !validator.isValidId(timeRecord.getId())) {
            throw new BusinessException("Dados do registro de tempo ou ID inválidos para atualização.");
        }
        if (!validator.isValidId(timeRecord.getJourneyId())) {
            throw new BusinessException("ID da jornada inválido.");
        }
        if (!validator.isValidId(timeRecord.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecord.getTimestamp() == null) {
            throw new BusinessException("Timestamp do registro é obrigatório.");
        }
        if (timeRecord.getEventType() == null) {
            throw new BusinessException("Tipo de evento do registro é obrigatório.");
        }
        if (timeRecord.getLatitude() == null || !validator.isValidLatitude(timeRecord.getLatitude())) {
            throw new BusinessException("Latitude inválida.");
        }
        if (timeRecord.getLongitude() == null || !validator.isValidLongitude(timeRecord.getLongitude())) {
            throw new BusinessException("Longitude inválida.");
        }

        try {
            Optional<TimeRecord> existingRecord = timeRecordDAO.findById(timeRecord.getId());
            if (!existingRecord.isPresent()) {
                throw new BusinessException("Registro de tempo com ID " + timeRecord.getId() + " não encontrado para atualização.");
            }

            // Validar se a jornada e o motorista existem
            if (!journeyDAO.findById(timeRecord.getJourneyId()).isPresent()) {
                throw new BusinessException("Jornada com ID " + timeRecord.getJourneyId() + " não encontrada.");
            }
            if (!driverDAO.findById(timeRecord.getDriverId()).isPresent()) {
                throw new BusinessException("Motorista com ID " + timeRecord.getDriverId() + " não encontrado.");
            }

            timeRecord.setCreatedAt(existingRecord.get().getCreatedAt()); // Mantém a data de criação original
            timeRecord.setUpdatedAt(LocalDateTime.now());
            boolean updated = timeRecordDAO.update(timeRecord);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar registro de tempo. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Registro de tempo atualizado com sucesso: ID {0}", timeRecord.getId());
            return timeRecord;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar registro de tempo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar registro de tempo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteTimeRecord(int recordId) throws BusinessException, SQLException {
        if (!validator.isValidId(recordId)) {
            throw new BusinessException("ID do registro de tempo inválido para exclusão.");
        }
        try {
            Optional<TimeRecord> existingRecord = timeRecordDAO.findById(recordId);
            if (!existingRecord.isPresent()) {
                throw new BusinessException("Registro de tempo com ID " + recordId + " não encontrado para exclusão.");
            }
            boolean deleted = timeRecordDAO.delete(recordId);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar registro de tempo. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Registro de tempo deletado com sucesso. ID: {0}", recordId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar registro de tempo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar registro de tempo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByJourneyId(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido para busca de registros de tempo.");
        }
        try {
            return timeRecordDAO.findByJourneyId(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por ID da jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registros de tempo por jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca de registros de tempo.");
        }
        try {
            return timeRecordDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registros de tempo por motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByEventType(EventType eventType) throws BusinessException, SQLException {
        if (eventType == null) {
            throw new BusinessException("Tipo de evento não pode ser nulo para busca de registros de tempo.");
        }
        try {
            return timeRecordDAO.findByEventType(eventType);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por tipo de evento: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registros de tempo por tipo de evento. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByDriverAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca de registros de tempo por período.");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("Datas de início e fim inválidas para busca de registros de tempo.");
        }
        try {
            return timeRecordDAO.findByDriverAndDateRange(driverId, startDate, endDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por motorista e período: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registros de tempo por motorista e período. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<TimeRecord> getTimeRecordsByJourneyIdOrderedByTimestamp(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido para busca de registros de tempo ordenados.");
        }
        try {
            return timeRecordDAO.findByJourneyIdOrderedByTimestamp(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo por jornada ordenados: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registros de tempo por jornada ordenados. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca de registro de tempo por data.");
        }
        if (date == null) {
            throw new BusinessException("Data inválida para busca de registro de tempo.");
        }
        try {
            // Assumindo que o DAO tem um método para buscar um único registro por driver e data
            // Se o DAO retornar uma lista, você precisará decidir qual registro retornar (ex: o primeiro)
            // Por enquanto, vamos chamar o método que busca por driver e range de data, pegando o primeiro se houver.
            List<TimeRecord> records = timeRecordDAO.findByDriverAndDateRange(driverId, date, date);
            return records.isEmpty() ? Optional.empty() : Optional.of(records.get(0));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de tempo por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar registro de tempo por motorista e data. Tente novamente mais tarde.", e);
        }
    }
}
