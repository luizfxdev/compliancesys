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

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
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
    private final ComplianceAuditDAO complianceAuditDAO;
    private final Validator validator;
    private final TimeUtil timeUtil;

    // Construtor completo
    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO,
                              ComplianceAuditDAO complianceAuditDAO, Validator validator, TimeUtil timeUtil) {
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.complianceAuditDAO = complianceAuditDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    // Construtor para casos onde TimeUtil não é necessário
    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO,
                              ComplianceAuditDAO complianceAuditDAO, Validator validator) {
        this(journeyDAO, timeRecordDAO, complianceAuditDAO, validator, null);
    }

    @Override
    public Journey createJourney(Journey journey) throws BusinessException, SQLException {
        if (journey == null) {
            throw new BusinessException("Jornada não pode ser nula.");
        }
        if (journey.getDriverId() <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }

        try {
            journey.setCreatedAt(LocalDateTime.now());
            journey.setUpdatedAt(LocalDateTime.now());
            int newId = journeyDAO.create(journey);
            journey.setId(newId);
            LOGGER.log(Level.INFO, "Jornada criada com sucesso para motorista {0}, ID: {1}",
                    new Object[]{journey.getDriverId(), journey.getId()});
            return journey;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar jornada.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyById(int journeyId) throws BusinessException, SQLException {
        if (journeyId <= 0) {
            throw new BusinessException("ID da jornada inválido.");
        }
        try {
            return journeyDAO.getById(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornada.", e);
        }
    }

    @Override
    public List<Journey> getAllJourneys() throws BusinessException, SQLException {
        try {
            return journeyDAO.getAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as jornadas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornadas.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByDriverId(int driverId) throws BusinessException, SQLException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        try {
            return journeyDAO.getJourneysByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornadas por motorista.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException, SQLException {
        if (driverId <= 0 || journeyDate == null) {
            throw new BusinessException("ID do motorista ou data da jornada inválidos.");
        }
        try {
            return journeyDAO.getJourneyByDriverIdAndDate(driverId, journeyDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornada por motorista e data.", e);
        }
    }

    @Override
    public Journey updateJourney(Journey journey) throws BusinessException, SQLException {
        if (journey == null || journey.getId() <= 0) {
            throw new BusinessException("Dados da jornada ou ID inválidos para atualização.");
        }

        try {
            // Verificar se a jornada existe antes de tentar atualizar
            // Linha 131 corrigida: de .isEmpty() para !.isPresent()
            if (!journeyDAO.getById(journey.getId()).isPresent()) {
                throw new BusinessException("Jornada com ID " + journey.getId() + " não encontrada para atualização.");
            }

            journey.setUpdatedAt(LocalDateTime.now());
            journeyDAO.update(journey);
            LOGGER.log(Level.INFO, "Jornada com ID {0} atualizada com sucesso.", journey.getId());
            return journey;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar jornada.", e);
        }
    }

    @Override
    public boolean deleteJourney(int journeyId) throws BusinessException, SQLException {
        if (journeyId <= 0) {
            throw new BusinessException("ID da jornada inválido para exclusão.");
        }
        try {
            // Verificar se a jornada existe antes de tentar deletar
            // Linha 152 corrigida: de .isEmpty() para !.isPresent()
            if (!journeyDAO.getById(journeyId).isPresent()) {
                LOGGER.log(Level.WARNING, "Tentativa de deletar jornada com ID {0} que não existe.", journeyId);
                return false;
            }
            boolean deleted = journeyDAO.delete(journeyId);
            if (deleted) {
                LOGGER.log(Level.INFO, "Jornada com ID {0} deletada com sucesso.", journeyId);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar jornada com ID {0}.", journeyId);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar jornada.", e);
        }
    }

    @Override
    public Journey calculateAndAuditJourney(int driverId, List<TimeRecord> timeRecords) throws BusinessException, SQLException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido para calcular a jornada.");
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
                journey.setTotalDuration(totalDuration.toMinutes());
                updateJourney(journey);
            } else {
                journey = new Journey();
                journey.setDriverId(driverId);
                journey.setJourneyDate(journeyDate);
                journey.setStartTime(startTime);
                journey.setEndTime(endTime);
                journey.setTotalDuration(totalDuration.toMinutes());
                journey.setVehicleId(firstRecord.getVehicleId());
                journey.setStartLocation(firstRecord.getLocation());
                journey.setEndLocation(lastRecord.getLocation());
                int newJourneyId = journeyDAO.create(journey);
                journey.setId(newJourneyId);
            }

            // Criar registro de auditoria
            ComplianceAudit audit = new ComplianceAudit();
            audit.setJourneyId(journey.getId());
            audit.setAuditDate(LocalDateTime.now());
            audit.setComplianceStatus(complianceStatus);
            audit.setAuditorName("Sistema Automático");
            audit.setNotes(notes);
            audit.setCreatedAt(LocalDateTime.now());
            audit.setUpdatedAt(LocalDateTime.now());
            complianceAuditDAO.create(audit);

            LOGGER.log(Level.INFO, "Jornada para motorista {0} em {1} calculada e auditada. Status: {2}",
                    new Object[]{driverId, journeyDate, complianceStatus});
            return journey;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao calcular e auditar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao calcular e auditar jornada.", e);
        }
    }
}
