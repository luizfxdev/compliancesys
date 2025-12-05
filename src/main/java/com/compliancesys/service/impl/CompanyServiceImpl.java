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
    private final JourneyDAO journeyDAO;
    private final Validator validator;

    public ComplianceServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO, Validator validator) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
        this.validator = validator;
    }

    @Override
    public ComplianceAudit createComplianceAudit(ComplianceAudit audit) throws BusinessException {
        if (audit == null) {
            throw new BusinessException("Auditoria de conformidade não pode ser nula.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("ID da jornada inválido para auditoria.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("Data da auditoria não pode ser nula.");
        }
        if (audit.getComplianceStatus() == null) {
            throw new BusinessException("Status de conformidade não pode ser nulo.");
        }
        if (!validator.isValidName(audit.getAuditorName())) {
            throw new BusinessException("Nome do auditor inválido.");
        }
        if (audit.getNotes() != null && audit.getNotes().length() > 500) {
            throw new BusinessException("Notas da auditoria excedem o limite de 500 caracteres.");
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
            throw new BusinessException("Erro interno ao criar auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<ComplianceAudit> getComplianceAuditById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da auditoria inválido.");
        }
        try {
            return complianceAuditDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditoria de conformidade por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAllComplianceAudits() throws BusinessException {
        try {
            return complianceAuditDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as auditorias de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getComplianceAuditsByJourneyId(int journeyId) throws BusinessException {
        if (journeyId <= 0) {
            throw new BusinessException("ID da jornada inválido.");
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
            throw new BusinessException("Auditoria de conformidade ou ID inválido para atualização.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("ID da jornada inválido para auditoria.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("Data da auditoria não pode ser nula.");
        }
        if (audit.getComplianceStatus() == null) {
            throw new BusinessException("Status de conformidade não pode ser nulo.");
        }
        if (!validator.isValidName(audit.getAuditorName())) {
            throw new BusinessException("Nome do auditor inválido.");
        }
        if (audit.getNotes() != null && audit.getNotes().length() > 500) {
            throw new BusinessException("Notas da auditoria excedem o limite de 500 caracteres.");
        }

        try {
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(audit.getId());
            if (existingAudit.isEmpty()) {
                throw new BusinessException("Auditoria de conformidade com ID " + audit.getId() + " não encontrada.");
            }

            audit.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            audit.setCreatedAt(existingAudit.get().getCreatedAt());

            boolean updated = complianceAuditDAO.update(audit);
            if (updated) {
                LOGGER.log(Level.INFO, "Auditoria de conformidade atualizada com sucesso: ID {0}", audit.getId());
                return audit;
            } else {
                throw new BusinessException("Falha ao atualizar auditoria de conformidade. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar auditoria de conformidade. Tente novamente mais tarde.", e);
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
            throw new BusinessException("Erro interno ao deletar auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public ComplianceAudit performComplianceAudit(int journeyId) throws BusinessException {
        if (journeyId <= 0) {
            throw new BusinessException("ID da jornada inválido para realizar auditoria.");
        }
        try {
            Optional<Journey> optionalJourney = journeyDAO.findById(journeyId);
            if (optionalJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + journeyId + " não encontrada para auditoria.");
            }
            Journey journey = optionalJourney.get(); // Desempacota o Optional

            // Lógica de auditoria (simplificada para exemplo)
            // Aqui você implementaria as regras da Lei do Caminhoneiro
            // Por exemplo, verificar totalDrivingTime, totalRestTime, etc.
            ComplianceStatus status = journey.getComplianceStatus() != null ? journey.getComplianceStatus() : ComplianceStatus.PENDING;

            // Cria um novo registro de auditoria
            ComplianceAudit newAudit = new ComplianceAudit();
            newAudit.setJourneyId(journeyId);
            newAudit.setAuditDate(LocalDateTime.now());
            newAudit.setComplianceStatus(status);
            newAudit.setAuditorName("Sistema Automático"); // Ou um nome de usuário logado
            newAudit.setNotes("Auditoria automática da jornada " + journeyId + ". Status: " + status.name());

            return createComplianceAudit(newAudit);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao realizar auditoria de conformidade para jornada {0}: {1}", new Object[]{journeyId, e.getMessage()});
            throw new BusinessException("Erro interno ao realizar auditoria de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public ComplianceReport generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("Período de datas inválido para o relatório.");
        }

        try {
            List<ComplianceAudit> audits = complianceAuditDAO.findByDriverIdAndDate(driverId, startDate, endDate);
            // Aqui você processaria a lista de auditorias para gerar um relatório mais detalhado
            // Por enquanto, apenas retorna um relatório básico
            int totalAudits = audits.size();
            long compliantCount = audits.stream().filter(a -> a.getComplianceStatus() == ComplianceStatus.COMPLIANT).count();
            long nonCompliantCount = audits.stream().filter(a -> a.getComplianceStatus() == ComplianceStatus.NON_COMPLIANT).count();

            return new ComplianceReport(driverId, startDate, endDate, totalAudits, (int) compliantCount, (int) nonCompliantCount, audits);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório de conformidade para motorista {0}: {1}", new Object[]{driverId, e.getMessage()});
            throw new BusinessException("Erro interno ao gerar relatório de conformidade. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<ComplianceAudit> generateOverallComplianceReport(LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("Período de datas inválido para o relatório geral.");
        }
        try {
            // Para um relatório geral, podemos buscar todas as auditorias e filtrar por data
            // Ou, se houver um método DAO mais otimizado, usá-lo.
            // Por simplicidade, vamos buscar todas e filtrar aqui, ou usar findByDriverIdAndDate com um driverId fictício se o DAO suportar.
            // No entanto, o DAO foi ajustado para findByDriverIdAndDate, então para um "overall" talvez seja melhor um novo método no DAO
            // ou buscar todos e filtrar. Por enquanto, vamos retornar todas as auditorias dentro do período.
            // Uma implementação mais robusta poderia agregar dados de múltiplas jornadas/motoristas.

            // Para este método, vamos retornar todas as auditorias dentro do período,
            // assumindo que o DAO tem um método para isso ou que findAll() é filtrado posteriormente.
            // Como não temos um findByDateRange no DAO, vamos usar findAll e filtrar.
            // Idealmente, o DAO teria um método findByDateRange para eficiência.
            List<ComplianceAudit> allAudits = complianceAuditDAO.findAll();
            return allAudits.stream()
                    .filter(audit -> !audit.getAuditDate().toLocalDate().isBefore(startDate) && !audit.getAuditDate().toLocalDate().isAfter(endDate))
                    .collect(java.util.stream.Collectors.toList());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório de conformidade geral: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao gerar relatório de conformidade geral. Tente novamente mais tarde.", e);
        }
    }
}
