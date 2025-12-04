package com.compliancesys.service.impl;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.dao.impl.JourneyDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Journey;
import com.compliancesys.service.ComplianceService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface ComplianceService.
 * Contém a lógica de negócio para a entidade ComplianceAudit, interagindo com a camada DAO.
 */
public class ComplianceServiceImpl implements ComplianceService {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServiceImpl.class.getName());
    private final ComplianceAuditDAO complianceAuditDAO;
    private final JourneyDAO journeyDAO; // Necessário para validar a existência da jornada

    /**
     * Construtor padrão que inicializa os DAOs.
     */
    public ComplianceServiceImpl() {
        this.complianceAuditDAO = new ComplianceAuditDAOImpl();
        this.journeyDAO = new JourneyDAOImpl();
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param complianceAuditDAO A implementação de ComplianceAuditDAO a ser utilizada.
     * @param journeyDAO A implementação de JourneyDAO a ser utilizada.
     */
    public ComplianceServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
    }

    @Override
    public ComplianceAudit createComplianceAudit(ComplianceAudit audit) throws BusinessException {
        // Validações de negócio antes de criar a auditoria
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("O ID da jornada é obrigatório e deve ser um valor positivo.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("A data da auditoria é obrigatória.");
        }
        if (audit.getAuditorName() == null || audit.getAuditorName().trim().isEmpty()) {
            throw new BusinessException("O nome do auditor não pode ser vazio.");
        }
        if (audit.getStatus() == null) {
            throw new BusinessException("O status da auditoria é obrigatório.");
        }

        try {
            // Verifica se a jornada associada existe
            Optional<Journey> existingJourney = journeyDAO.findById(audit.getJourneyId());
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada. Não é possível criar a auditoria.");
            }

            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            audit.setCreatedAt(now);
            audit.setUpdatedAt(now);

            int id = complianceAuditDAO.create(audit);
            if (id > 0) {
                audit.setId(id);
                LOGGER.log(Level.INFO, "Auditoria de conformidade criada com sucesso para a jornada ID: {0}", audit.getJourneyId());
                return audit;
            } else {
                throw new BusinessException("Falha ao criar a auditoria de conformidade. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar a auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<ComplianceAudit> getComplianceAuditById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da auditoria deve ser um valor positivo.");
        }
        try {
            return complianceAuditDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditoria por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a auditoria. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAllComplianceAudits() throws BusinessException {
        try {
            return complianceAuditDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as auditorias: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar as auditorias. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getComplianceAuditsByJourneyId(int journeyId) throws BusinessException {
        if (journeyId <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo.");
        }
        try {
            return complianceAuditDAO.findByJourneyId(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por ID de jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias por jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getComplianceAuditsByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo.");
        }
        if (startDate == null || endDate == null) {
            throw new BusinessException("As datas de início e fim são obrigatórias.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("A data de início não pode ser posterior à data de fim.");
        }
        try {
            return complianceAuditDAO.findByDriverIdAndDateRange(driverId, startDate, endDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por motorista e período: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias por motorista e período. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getComplianceAuditsByDateRange(LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (startDate == null || endDate == null) {
            throw new BusinessException("As datas de início e fim são obrigatórias.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("A data de início não pode ser posterior à data de fim.");
        }
        try {
            return complianceAuditDAO.findByDateRange(startDate, endDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por período: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias por período. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public ComplianceAudit updateComplianceAudit(ComplianceAudit audit) throws BusinessException {
        if (audit.getId() <= 0) {
            throw new BusinessException("O ID da auditoria deve ser um valor positivo para atualização.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("O ID da jornada é obrigatório e deve ser um valor positivo.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("A data da auditoria é obrigatória.");
        }
        if (audit.getAuditorName() == null || audit.getAuditorName().trim().isEmpty()) {
            throw new BusinessException("O nome do auditor não pode ser vazio.");
        }
        if (audit.getStatus() == null) {
            throw new BusinessException("O status da auditoria é obrigatório.");
        }

        try {
            // Verifica se a auditoria a ser atualizada existe
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(audit.getId());
            if (existingAudit.isEmpty()) {
                throw new BusinessException("Auditoria com ID " + audit.getId() + " não encontrada para atualização.");
            }

            // Verifica se a jornada associada existe
            Optional<Journey> existingJourney = journeyDAO.findById(audit.getJourneyId());
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada. Não é possível atualizar a auditoria.");
            }

            // Define a data de atualização
            audit.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            audit.setCreatedAt(existingAudit.get().getCreatedAt());

            boolean updated = complianceAuditDAO.update(audit);
            if (updated) {
                LOGGER.log(Level.INFO, "Auditoria de conformidade atualizada com sucesso: ID {0}", audit.getId());
                return audit;
            } else {
                throw new BusinessException("Falha ao atualizar a auditoria de conformidade. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar a auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteComplianceAudit(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da auditoria deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se a auditoria existe antes de tentar deletar
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(id);
            if (existingAudit.isEmpty()) {
                throw new BusinessException("Auditoria com ID " + id + " não encontrada para exclusão.");
            }

            boolean deleted = complianceAuditDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Auditoria de conformidade com ID {0} deletada com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar auditoria de conformidade com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar a auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }
}
