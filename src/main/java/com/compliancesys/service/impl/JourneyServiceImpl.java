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

import com.compliancesys.dao.ComplianceAuditDAO; // Import adicionado
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException; // Import adicionado
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.ComplianceStatus; // Import adicionado
import com.compliancesys.service.JourneyService;
import com.compliancesys.util.TimeUtil; // Import adicionado
import com.compliancesys.util.Validator;

public class JourneyServiceImpl implements JourneyService {

    private static final Logger LOGGER = Logger.getLogger(JourneyServiceImpl.class.getName());

    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO;
    private final ComplianceAuditDAO complianceAuditDAO; // Adicionado
    private final Validator validator;
    private final TimeUtil timeUtil; // opcional, pode ser null se não fornecido

    // Construtor antigo (compatibilidade) - pode ser removido se não for mais usado
    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO, Validator validator) {
        this(journeyDAO, timeRecordDAO, null, validator, null); // Ajustado para incluir complianceAuditDAO
    }

    // Construtor novo (aceita TimeUtil e ComplianceAuditDAO)
    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO, ComplianceAuditDAO complianceAuditDAO, Validator validator, TimeUtil timeUtil) {
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.complianceAuditDAO = complianceAuditDAO; // Inicializado
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
             Optional<Journey> existingJourneyOptional = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
if (existingJourneyOptional.isPresent()) {
    throw new BusinessException("Já existe uma jornada para este motorista nesta data.");


        }

            journey.setCreatedAt(LocalDateTime.now());
            journey.setUpdatedAt(LocalDateTime.now());
            int id = journeyDAO.create(journey);
            if (id > 0) {
                journey.setId(id);
                LOGGER.log(Level.INFO, "Jornada criada com sucesso: ID {0}", id);
                return journey;
            } else {
                throw new BusinessException("Falha ao criar a jornada. Nenhum ID retornado.");
            }
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
            throw new BusinessException("Erro interno ao buscar todas as jornadas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Journey updateJourney(Journey journey) throws BusinessException {
        if (journey == null || journey.getId() <= 0) {
            throw new BusinessException("Jornada ou ID da jornada inválido para atualização.");
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
            if (!existingJourney.isPresent()) {
                throw new BusinessException("Jornada com ID " + journey.getId() + " não encontrada para atualização.");
            }

            // Verifica se a atualização resultaria em uma jornada duplicada para o mesmo motorista na mesma data
            Optional<Journey> journeyOptional = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
if (journeyOptional.isPresent() && journeyOptional.get().getId() != journey.getId()) {
    throw new BusinessException("Já existe outra jornada para este motorista nesta data.");
}
            

            journey.setUpdatedAt(LocalDateTime.now());
            boolean updated = journeyDAO.update(journey);
            if (updated) {
                LOGGER.log(Level.INFO, "Jornada atualizada com sucesso: ID {0}", journey.getId());
                return journey; // Retorna a jornada atualizada
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
            throw new BusinessException("ID da jornada inválido para exclusão.");
        }
        try {
            Optional<Journey> existingJourney = journeyDAO.findById(id);
            if (!existingJourney.isPresent()) {
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
        LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por ID do motorista e data: " + e.getMessage(), e);
        throw new BusinessException("Erro interno ao buscar jornada.", e);
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
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornadas.", e);
        }
    }

    @Override
    public Journey calculateAndAuditJourney(int driverId, List<TimeRecord> timeRecords) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecords == null || timeRecords.isEmpty()) {
            throw new BusinessException("Registros de ponto não podem ser nulos ou vazios para calcular a jornada.");
        }
        if (complianceAuditDAO == null) {
            throw new BusinessException("ComplianceAuditDAO não inicializado. Não é possível auditar a jornada.");
        }

        // Ordenar registros de ponto por tempo
        timeRecords.sort(Comparator.comparing(TimeRecord::getRecordTime));

        // Assumir que o primeiro registro é o início da jornada e o último é o fim
        TimeRecord firstRecord = timeRecords.get(0);
        TimeRecord lastRecord = timeRecords.get(timeRecords.size() - 1);

        LocalDate journeyDate = firstRecord.getRecordTime().toLocalDate();
        LocalDateTime startTime = firstRecord.getRecordTime();
        LocalDateTime endTime = lastRecord.getRecordTime();

        // Calcular duração total da jornada
        Duration totalDuration = Duration.between(startTime, endTime);

        // Lógica de cálculo de conformidade (exemplo simplificado)
        // Você precisará implementar a lógica real de acordo com suas regras de negócio
        ComplianceStatus complianceStatus = ComplianceStatus.COMPLIANT;
        String notes = "Jornada calculada automaticamente.";

        // Exemplo: Se a jornada exceder 10 horas, é não-conforme
        if (totalDuration.toHours() > 10) {
            complianceStatus = ComplianceStatus.NON_COMPLIANT;
            notes += " Duração total excedeu 10 horas.";
        }

        // Criar ou atualizar a jornada
        Journey journey;
        try {
            Optional<Journey> existingJourneyOpt = getJourneyByDriverIdAndDate(driverId, journeyDate);
            if (existingJourneyOpt.isPresent()) {
                journey = existingJourneyOpt.get();
                journey.setStartTime(startTime);
                journey.setEndTime(endTime);
                journey.setTotalDuration(totalDuration.toMinutes()); // Armazenar em minutos, por exemplo
                // Atualizar outros campos conforme necessário
                updateJourney(journey); // Chama o método de atualização
            } else {
                journey = new Journey();
                journey.setDriverId(driverId);
                journey.setJourneyDate(journeyDate);
                journey.setStartTime(startTime);
                journey.setEndTime(endTime);
                journey.setTotalDuration(totalDuration.toMinutes());
                journey.setVehicleId(firstRecord.getVehicleId()); // Assumindo o veículo do primeiro registro
                journey.setStartLocation(firstRecord.getLocation());
                journey.setEndLocation(lastRecord.getLocation());
                createJourney(journey); // Chama o método de criação
            }

            // Criar registro de auditoria
            ComplianceAudit audit = new ComplianceAudit();
            audit.setJourneyId(journey.getId());
            audit.setAuditDate(LocalDateTime.now()); // Data da auditoria é agora
            audit.setComplianceStatus(complianceStatus);
            audit.setAuditorName("Sistema Automático");
            audit.setNotes(notes);
            audit.setCreatedAt(LocalDateTime.now());
            audit.setUpdatedAt(LocalDateTime.now());
            complianceAuditDAO.create(audit); // Salva a auditoria

            LOGGER.log(Level.INFO, "Jornada para motorista {0} em {1} calculada e auditada. Status: {2}",
                    new Object[]{driverId, journeyDate, complianceStatus});
            return journey;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao calcular e auditar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao calcular e auditar jornada.", e);
        }
    }
}
