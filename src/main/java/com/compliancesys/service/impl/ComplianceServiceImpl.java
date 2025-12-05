package com.compliancesys.service.impl;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceReport;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.ComplianceService;
import com.compliancesys.util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComplianceServiceImpl implements ComplianceService {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServiceImpl.class.getName());

    private final ComplianceAuditDAO complianceAuditDAO;
    private final JourneyDAO journeyDAO; // Adicionado para buscar jornadas
    private final Validator validator;

    public ComplianceServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO, Validator validator) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
        this.validator = validator;
    }

    @Override
    public ComplianceAudit createComplianceAudit(ComplianceAudit audit) throws BusinessException {
        // Validações básicas
        if (audit == null) {
            throw new BusinessException("O objeto de auditoria de conformidade não pode ser nulo.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("O ID da jornada é obrigatório para a auditoria de conformidade.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("A data da auditoria é obrigatória.");
        }
        if (!validator.isValidName(audit.getAuditorName())) {
            throw new BusinessException("Nome do auditor inválido.");
        }
        if (audit.getComplianceStatus() == null) { // CORRIGIDO: getComplianceStatus()
            throw new BusinessException("O status de conformidade é obrigatório.");
        }

        try {
            audit.setCreatedAt(LocalDateTime.now());
            audit.setUpdatedAt(LocalDateTime.now());
            int id = complianceAuditDAO.create(audit);
            audit.setId(id);
            LOGGER.log(Level.INFO, "Auditoria de conformidade criada com sucesso: ID {0}", id);
            return audit;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar a auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<ComplianceAudit> getComplianceAuditById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da auditoria de conformidade deve ser um valor positivo.");
        }
        try {
            return complianceAuditDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditoria de conformidade por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAllComplianceAudits() throws BusinessException {
        try {
            return complianceAuditDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as auditorias de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar as auditorias de conformidade. Tente novamente mais tarde.", e);
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
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias de conformidade por ID da jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public ComplianceAudit updateComplianceAudit(ComplianceAudit audit) throws BusinessException {
        if (audit == null || audit.getId() <= 0) {
            throw new BusinessException("O objeto de auditoria de conformidade ou seu ID são inválidos para atualização.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("O ID da jornada é obrigatório para a auditoria de conformidade.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("A data da auditoria é obrigatória.");
        }
        if (!validator.isValidName(audit.getAuditorName())) {
            throw new BusinessException("Nome do auditor inválido.");
        }
        if (audit.getComplianceStatus() == null) { // CORRIGIDO: getComplianceStatus()
            throw new BusinessException("O status de conformidade é obrigatório.");
        }

        try {
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(audit.getId());
            if (existingAudit.isEmpty()) {
                throw new BusinessException("Auditoria de conformidade com ID " + audit.getId() + " não encontrada para atualização.");
            }

            audit.setCreatedAt(existingAudit.get().getCreatedAt()); // Mantém a data de criação original
            audit.setUpdatedAt(LocalDateTime.now());

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
            throw new BusinessException("O ID da auditoria de conformidade deve ser um valor positivo para exclusão.");
        }
        try {
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(id);
            if (existingAudit.isEmpty()) {
                throw new BusinessException("Auditoria de conformidade com ID " + id + " não encontrada para exclusão.");
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

    @Override
    public ComplianceAudit performComplianceAudit(int journeyId) throws BusinessException {
        if (journeyId <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo para realizar a auditoria.");
        }

        try {
            Optional<Journey> optionalJourney = journeyDAO.findById(journeyId);
            if (optionalJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + journeyId + " não encontrada para auditoria.");
            }
            Journey journey = optionalJourney.get(); // Desempacota o Optional

            // Lógica de auditoria de conformidade (exemplo simplificado)
            // Aqui você implementaria as regras da Lei do Caminhoneiro
            // para verificar se a jornada está em conformidade.

            ComplianceStatus status = journey.getComplianceStatus() != null ? journey.getComplianceStatus() : ComplianceStatus.PENDING; // CORRIGIDO
            String notes = "Auditoria realizada em " + LocalDateTime.now() + ". Status inicial: " + status.name();

            // Exemplo: Se a jornada excedeu o limite diário, o status pode ser NON_COMPLIANT
            if (journey.isDailyLimitExceeded()) {
                status = ComplianceStatus.NON_COMPLIANT;
                notes += " - Limite diário excedido.";
            } else if (status == ComplianceStatus.PENDING) {
                // Se não excedeu e ainda está PENDING, pode ser COMPLIANT
                status = ComplianceStatus.COMPLIANT;
                notes += " - Conforme as regras básicas.";
            }

            // Cria ou atualiza o registro de auditoria
            ComplianceAudit audit = new ComplianceAudit(
                    0, // ID será gerado
                    journeyId,
                    LocalDateTime.now(),
                    status,
                    "Sistema Automático", // Auditor
                    notes,
                    null, null // createdAt e updatedAt serão definidos no DAO
            );

            return createComplianceAudit(audit);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao realizar auditoria de conformidade para jornada ID " + journeyId + ": " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao realizar a auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public ComplianceReport generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo.");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("As datas de início e fim do relatório são inválidas.");
        }

        try {
            List<ComplianceAudit> audits = complianceAuditDAO.findByDriverIdAndDate(driverId, startDate, endDate); // CORRIGIDO
            // Aqui você processaria a lista de auditorias para gerar um relatório mais detalhado
            // Por enquanto, vamos retornar um relatório simples
            int compliantCount = (int) audits.stream().filter(a -> a.getComplianceStatus() == ComplianceStatus.COMPLIANT).count();
            int nonCompliantCount = (int) audits.stream().filter(a -> a.getComplianceStatus() == ComplianceStatus.NON_COMPLIANT).count();
            int pendingCount = (int) audits.stream().filter(a -> a.getComplianceStatus() == ComplianceStatus.PENDING).count();

            return new ComplianceReport(driverId, startDate, endDate, compliantCount, nonCompliantCount, pendingCount, audits);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório de conformidade para motorista ID " + driverId + ": " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao gerar o relatório de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> generateOverallComplianceReport(LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("As datas de início e fim do relatório são inválidas.");
        }

        try {
            // Para um relatório geral, podemos buscar todas as auditorias e filtrar por data
            // Ou, se houver um método no DAO para isso, usá-lo.
            // Por simplicidade, vamos buscar todas e filtrar aqui.
            // Idealmente, o DAO teria um método findByDateRange()
            List<ComplianceAudit> allAudits = complianceAuditDAO.findAll();
            return allAudits.stream()
                    .filter(audit -> !audit.getAuditDate().toLocalDate().isBefore(startDate) &&
                                     !audit.getAuditDate().toLocalDate().isAfter(endDate))
                    .toList();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório de conformidade geral: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao gerar o relatório de conformidade geral. Tente novamente mais tarde.", e);
        }
    }
}
