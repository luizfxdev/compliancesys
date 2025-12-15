// src/main/java/com/compliancesys/service/impl/ComplianceAuditServiceImpl.java
package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Journey;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.ComplianceAuditService;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class ComplianceAuditServiceImpl implements ComplianceAuditService {
    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditServiceImpl.class.getName());

    private final ComplianceAuditDAO complianceAuditDAO;
    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO;
    private final MobileCommunicationDAO mobileCommunicationDAO;
    private final DriverDAO driverDAO;
    private final Validator validator;
    private final TimeUtil timeUtil;

    // Construtor para injeção de dependência
    public ComplianceAuditServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO,
                                      TimeRecordDAO timeRecordDAO, MobileCommunicationDAO mobileCommunicationDAO,
                                      DriverDAO driverDAO, Validator validator, TimeUtil timeUtil) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.mobileCommunicationDAO = mobileCommunicationDAO;
        this.driverDAO = driverDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    @Override
    public ComplianceAudit performAudit(Journey journey, List<TimeRecord> timeRecords) throws BusinessException, SQLException {
        if (journey == null || !validator.isValidId(journey.getId())) {
            throw new BusinessException("Jornada inválida para auditoria.");
        }
        if (timeRecords == null || timeRecords.isEmpty()) {
            throw new BusinessException("Registros de tempo são necessários para a auditoria.");
        }

        ComplianceAudit audit = new ComplianceAudit();
        audit.setJourneyId(journey.getId());
        audit.setDriverId(journey.getDriverId());
        audit.setAuditDate(journey.getJourneyDate());
        audit.setAuditTimestamp(LocalDateTime.now());
        audit.setAuditorName("Sistema Automático");

        List<String> violations = new ArrayList<>();
        ComplianceStatus overallStatus = ComplianceStatus.COMPLIANT;

        // 1. Verificar duração total de trabalho
        Duration totalWorkDuration = timeUtil.calculateTotalWorkDuration(timeRecords);
        audit.setTotalWorkDuration(totalWorkDuration);
        // Regra: Exemplo - máximo de 10 horas de trabalho por dia
        if (totalWorkDuration.toHours() > 10) {
            violations.add("Duração total de trabalho excedeu 10 horas: " + totalWorkDuration.toHours() + "h");
            overallStatus = ComplianceStatus.NON_COMPLIANT;
        }

        // 2. Verificar tempo máximo de direção contínua e descanso intrajornada
        Duration maxContinuousDriving = timeUtil.calculateMaxContinuousDriving(timeRecords);
        audit.setMaxContinuousDriving(maxContinuousDriving);
        // Regra: Exemplo - máximo de 5.5 horas de direção contínua sem descanso de 30 min
        if (maxContinuousDriving.toMinutes() > 330) { // 5.5 horas * 60 minutos
            violations.add("Tempo máximo de direção contínua excedido: " + maxContinuousDriving.toHours() + "h " + (maxContinuousDriving.toMinutes() % 60) + "min");
            overallStatus = ComplianceStatus.NON_COMPLIANT;
        }
        // Verificar descanso intrajornada - requer duração mínima de descanso e intervalo máximo de trabalho
        Duration minRestDuration = Duration.ofMinutes(30); // 30 minutos de descanso mínimo
        Duration maxWorkInterval = Duration.ofHours(5).plusMinutes(30); // 5.5 horas máximo antes do descanso
        if (timeUtil.hasInsufficientIntraJourneyRest(timeRecords, minRestDuration, maxWorkInterval)) {
            violations.add("Descanso intrajornada insuficiente.");
            overallStatus = ComplianceStatus.NON_COMPLIANT;
        }

        // 3. Verificar comunicação móvel (se houver regras específicas)
        List<MobileCommunication> mobileCommunications = mobileCommunicationDAO.findByJourneyId(journey.getId());
        if (mobileCommunications != null && !mobileCommunications.isEmpty()) {
            // Lógica adicional para verificar comunicação móvel pode ser adicionada aqui
        }

        audit.setViolations(String.join("; ", violations));
        audit.setStatus(overallStatus);

        try {
            int id = complianceAuditDAO.create(audit);
            if (id <= 0) {
                throw new BusinessException("Falha ao registrar auditoria de conformidade.");
            }
            audit.setId(id);
            LOGGER.log(Level.INFO, "Auditoria de conformidade registrada para jornada {0} com status: {1}",
                    new Object[]{journey.getId(), overallStatus.name()});
            return audit;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar auditoria: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar auditoria. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<ComplianceAudit> getAuditById(int auditId) throws BusinessException, SQLException {
        if (!validator.isValidId(auditId)) {
            throw new BusinessException("ID da auditoria inválido.");
        }
        try {
            return complianceAuditDAO.findById(auditId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditoria por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar auditoria. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAuditsByJourneyId(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido.");
        }
        try {
            return complianceAuditDAO.findByJourneyId(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por ID da jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar auditorias. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAuditsByDriverId(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        try {
            return complianceAuditDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar auditorias. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAuditsByDateRange(LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("Intervalo de datas inválido.");
        }
        try {
            return complianceAuditDAO.findByAuditDateRange(startDate, endDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por intervalo de datas: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar auditorias. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAuditsByJourneyIdAndDate(int journeyId, LocalDate auditDate) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId) || auditDate == null) {
            throw new BusinessException("ID da jornada ou data de auditoria inválidos.");
        }
        try {
            Optional<ComplianceAudit> auditOpt = complianceAuditDAO.findByJourneyIdAndAuditDate(journeyId, auditDate);
            List<ComplianceAudit> audits = new ArrayList<>();
            auditOpt.ifPresent(audits::add);
            return audits;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por ID da jornada e data: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar auditorias. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAllAudits() throws BusinessException, SQLException {
        try {
            return complianceAuditDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as auditorias: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar auditorias. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean updateAudit(ComplianceAudit audit) throws BusinessException, SQLException {
        if (audit == null || !validator.isValidId(audit.getId())) {
            throw new BusinessException("Dados da auditoria ou ID inválido para atualização.");
        }
        try {
            audit.setUpdatedAt(LocalDateTime.now());
            boolean updated = complianceAuditDAO.update(audit);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar auditoria. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Auditoria atualizada com sucesso: ID {0}", audit.getId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar auditoria: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar auditoria. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteAudit(int auditId) throws BusinessException, SQLException {
        if (!validator.isValidId(auditId)) {
            throw new BusinessException("ID da auditoria inválido para exclusão.");
        }
        try {
            boolean deleted = complianceAuditDAO.delete(auditId);
            if (!deleted) {
                throw new BusinessException("Auditoria não encontrada para exclusão.");
            }
            LOGGER.log(Level.INFO, "Auditoria deletada com sucesso. ID: {0}", auditId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar auditoria: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar auditoria. Tente novamente mais tarde.", e);
        }
    }
}