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
import com.compliancesys.service.JourneyService;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class JourneyServiceImpl implements JourneyService {

    private static final Logger LOGGER = Logger.getLogger(JourneyServiceImpl.class.getName());

    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO;
    private final Validator validator;
    private final TimeUtil timeUtil; // opcional, pode ser null se não fornecido

    // Construtor antigo (compatibilidade)
    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO, Validator validator) {
        this(journeyDAO, timeRecordDAO, validator, null);
    }

    // Construtor novo (aceita TimeUtil)
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
        if (journey.getVehicleId() <= 0) {
            throw new BusinessException("ID do veículo inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada é obrigatória.");
        }
        if (journey.getStartTime() == null) {
            throw new BusinessException("Hora de início da jornada é obrigatória.");
        }
        if (journey.getEndTime() == null) {
            throw new BusinessException("Hora de fim da jornada é obrigatória.");
        }
        if (journey.getStartTime().isAfter(journey.getEndTime())) {
            throw new BusinessException("Hora de início não pode ser depois da hora de fim.");
        }

        // Validações adicionais com o validator
        if (!validator.isPastOrPresentDate(journey.getJourneyDate())) {
            throw new BusinessException("Data da jornada inválida ou no futuro.");
        }
        if (!validator.isValidTime(journey.getStartTime().toLocalTime())) {
            throw new BusinessException("Hora de início da jornada inválida.");
        }
        if (!validator.isValidTime(journey.getEndTime().toLocalTime())) {
            throw new BusinessException("Hora de fim da jornada inválida.");
        }
        if (journey.getStartLocation() != null && !journey.getStartLocation().isEmpty() && !validator.isValidLocation(journey.getStartLocation())) {
            throw new BusinessException("Local de início da jornada inválido.");
        }
        if (journey.getEndLocation() != null && !journey.getEndLocation().isEmpty() && !validator.isValidLocation(journey.getEndLocation())) {
            throw new BusinessException("Local de fim da jornada inválido.");
        }

        try {
            // Verifica se já existe uma jornada para o mesmo motorista e data
            Optional<Journey> existingJourney = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
            if (existingJourney.isPresent()) {
                throw new BusinessException("Já existe uma jornada para o motorista " + journey.getDriverId() + " na data " + journey.getJourneyDate() + ".");
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
        if (journey == null) {
            throw new BusinessException("Jornada não pode ser nula.");
        }
        if (journey.getId() <= 0) {
            throw new BusinessException("ID da jornada inválido para atualização.");
        }
        if (journey.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (journey.getVehicleId() <= 0) {
            throw new BusinessException("ID do veículo inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada é obrigatória.");
        }
        if (journey.getStartTime() == null) {
            throw new BusinessException("Hora de início da jornada é obrigatória.");
        }
        if (journey.getEndTime() == null) {
            throw new BusinessException("Hora de fim da jornada é obrigatória.");
        }
        if (journey.getStartTime().isAfter(journey.getEndTime())) {
            throw new BusinessException("Hora de início não pode ser depois da hora de fim.");
        }

        // Validações adicionais com o validator
        if (!validator.isPastOrPresentDate(journey.getJourneyDate())) {
            throw new BusinessException("Data da jornada inválida ou no futuro.");
        }
        if (!validator.isValidTime(journey.getStartTime().toLocalTime())) {
            throw new BusinessException("Hora de início da jornada inválida.");
        }
        if (!validator.isValidTime(journey.getEndTime().toLocalTime())) {
            throw new BusinessException("Hora de fim da jornada inválida.");
        }
        if (journey.getStartLocation() != null && !journey.getStartLocation().isEmpty() && !validator.isValidLocation(journey.getStartLocation())) {
            throw new BusinessException("Local de início da jornada inválido.");
        }
        if (journey.getEndLocation() != null && !journey.getEndLocation().isEmpty() && !validator.isValidLocation(journey.getEndLocation())) {
            throw new BusinessException("Local de fim da jornada inválido.");
        }

        try {
            Optional<Journey> existingJourney = journeyDAO.findById(journey.getId());
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + journey.getId() + " não encontrada para atualização.");
            }

            journey.setCreatedAt(existingJourney.get().getCreatedAt()); // Mantém o createdAt original
            journey.setUpdatedAt(LocalDateTime.now());

            boolean updated = journeyDAO.update(journey);
            if (updated) {
                LOGGER.log(Level.INFO, "Jornada atualizada com sucesso: ID {0}", journey.getId());
                return journey; // Retorna a jornada atualizada
            } else {
                LOGGER.log(Level.WARNING, "Falha ao atualizar jornada. Nenhuma linha afetada.");
                throw new BusinessException("Falha ao atualizar jornada. Nenhuma alteração foi aplicada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteJourney(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da jornada inválido para exclusão.");
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
            throw new BusinessException("Data da jornada é obrigatória.");
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
            throw new BusinessException("Lista de registros de ponto não pode ser nula ou vazia.");
        }

        // Ordenar os registros de ponto por recordTime
        timeRecords.sort(Comparator.comparing(TimeRecord::getRecordTime));

        // Extrair a data da jornada (todos os registros devem ser do mesmo dia)
        LocalDate journeyDate = timeRecords.get(0).getRecordTime().toLocalDate();
        LocalDateTime startTime = timeRecords.get(0).getRecordTime();
        LocalDateTime endTime = timeRecords.get(timeRecords.size() - 1).getRecordTime();

        Duration totalDrivingTime = Duration.ZERO;
        Duration totalRestTime = Duration.ZERO;
        Duration totalBreakTime = Duration.ZERO; // Usando totalBreakTime conforme Journey.java

        for (int i = 0; i < timeRecords.size(); i++) {
            TimeRecord currentRecord = timeRecords.get(i);

            // Validação de data: todos os registros devem ser do mesmo dia
            if (!currentRecord.getRecordTime().toLocalDate().equals(journeyDate)) {
                throw new BusinessException("Todos os registros de ponto devem ser do mesmo dia para calcular uma jornada.");
            }

            if (i > 0) {
                TimeRecord previousRecord = timeRecords.get(i - 1);
                Duration duration = Duration.between(previousRecord.getRecordTime(), currentRecord.getRecordTime());

                // Refatorado de if-else if para switch
                switch (previousRecord.getEventType()) {
                    case DRIVING:
                        totalDrivingTime = totalDrivingTime.plus(duration);
                        break;
                    case REST:
                        totalRestTime = totalRestTime.plus(duration);
                        break;
                    case IDLE:
                        // Considerar IDLE como parte do tempo de trabalho ou descanso dependendo da regra
                        // Por enquanto, vamos adicionar ao totalBreakTime como um tempo "não produtivo"
                        totalBreakTime = totalBreakTime.plus(duration);
                        break;
                    case OFF_DUTY:
                        // OFF_DUTY geralmente não conta para tempo de trabalho/descanso dentro da jornada
                        // Mas a duração entre OFF_DUTY e o próximo evento pode ser considerada
                        // para fins de cálculo de tempo de descanso entre jornadas, etc.
                        // Para esta jornada, não adicionamos a nenhum total específico de trabalho/descanso.
                        break;
                    // Adicionado um default para tratar EventTypes não esperados ou novos
                    default:
                        LOGGER.log(Level.WARNING, "EventType '{0}' não tratado no cálculo da jornada. Duração de {1} não foi contabilizada.",
                                new Object[]{previousRecord.getEventType(), duration});
                        break;
                }
            }
        }

        // Criar ou atualizar a jornada
        Journey calculatedJourney = new Journey();
        calculatedJourney.setDriverId(driverId);
        calculatedJourney.setJourneyDate(journeyDate);
        calculatedJourney.setStartTime(startTime);
        calculatedJourney.setEndTime(endTime);
        // Assumindo que todos os registros são do mesmo veículo. Se não for o caso,
        // essa lógica precisaria ser mais sofisticada para determinar o vehicleId da jornada.
        calculatedJourney.setVehicleId(timeRecords.get(0).getVehicleId());
        calculatedJourney.setTotalDrivingTime(totalDrivingTime);
        calculatedJourney.setTotalRestTime(totalRestTime);
        calculatedJourney.setTotalBreakTime(totalBreakTime);

        // Definindo o status inicial como UNKNOWN (agora existe no enum)
        calculatedJourney.setStatus(ComplianceStatus.UNKNOWN);
        calculatedJourney.setDailyLimitExceeded(false);

        // TODO: Adicionar lógica de auditoria de conformidade aqui
        // Ex: Verificar limites de tempo de direção, descanso, etc.
        // if (timeUtil != null) {
        //      if (timeUtil.isDrivingLimitExceeded(totalDrivingTime)) {
        //          calculatedJourney.setDailyLimitExceeded(true);
        //          calculatedJourney.setStatus(ComplianceStatus.NON_COMPLIANT);
        //      } else {
        //          calculatedJourney.setStatus(ComplianceStatus.COMPLIANT);
        //      }
        // } else {
        //      LOGGER.log(Level.WARNING, "TimeUtil não fornecido. Não foi possível realizar auditoria de conformidade completa.");
        // }
        // Exemplo:
        // if (!validator.isWithinMaxDuration(totalDrivingTime, Duration.ofHours(10))) {
        //      throw new BusinessException("Tempo total de direção excede o limite permitido.");
        // }

        try {
            // Tenta encontrar uma jornada existente para o motorista e data
            Optional<Journey> existingJourney = journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
            if (existingJourney.isPresent()) {
                // Se existir, atualiza a jornada existente
                calculatedJourney.setId(existingJourney.get().getId());
                calculatedJourney.setCreatedAt(existingJourney.get().getCreatedAt()); // Mantém o original
                calculatedJourney.setUpdatedAt(LocalDateTime.now());
                journeyDAO.update(calculatedJourney);
                LOGGER.log(Level.INFO, "Jornada existente atualizada com sucesso após auditoria: ID {0}", calculatedJourney.getId());
            } else {
                // Se não existir, cria uma nova jornada
                calculatedJourney.setCreatedAt(LocalDateTime.now());
                calculatedJourney.setUpdatedAt(LocalDateTime.now());
                int newId = journeyDAO.create(calculatedJourney);
                calculatedJourney.setId(newId);
                LOGGER.log(Level.INFO, "Nova jornada criada com sucesso após auditoria: ID {0}", newId);
            }
            return calculatedJourney;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao salvar jornada auditada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao salvar jornada auditada.", e);
        }
    }
}
