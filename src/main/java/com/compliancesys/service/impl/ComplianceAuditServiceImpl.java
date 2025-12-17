package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.ComplianceAuditService;
import com.compliancesys.util.Validator;

public class ComplianceAuditServiceImpl implements ComplianceAuditService {

    private final ComplianceAuditDAO complianceAuditDAO;
    private final JourneyDAO journeyDAO;
    private final DriverDAO driverDAO;
    private final Validator validator;

    public ComplianceAuditServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO, DriverDAO driverDAO, Validator validator) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
        this.driverDAO = driverDAO;
        this.validator = validator;
    }

    @Override
    public ComplianceAudit createAudit(ComplianceAudit audit) throws SQLException, BusinessException {
        validator.validate(audit);

        if (!journeyDAO.findById(audit.getJourneyId()).isPresent()) {
            throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada.");
        }

        if (audit.getDriverId() > 0 && !driverDAO.findById(audit.getDriverId()).isPresent()) {
            throw new BusinessException("Motorista com ID " + audit.getDriverId() + " não encontrado.");
        }

        LocalDateTime now = LocalDateTime.now();
        audit.setCreatedAt(now);
        audit.setUpdatedAt(now);

        int id = complianceAuditDAO.create(audit);
        audit.setId(id);
        return audit;
    }

    @Override
    public boolean updateAudit(ComplianceAudit audit) throws SQLException, BusinessException {
        validator.validate(audit);

        if (!complianceAuditDAO.findById(audit.getId()).isPresent()) {
            throw new BusinessException("Auditoria com ID " + audit.getId() + " não encontrada para atualização.");
        }

        if (!journeyDAO.findById(audit.getJourneyId()).isPresent()) {
            throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada.");
        }

        if (audit.getDriverId() > 0 && !driverDAO.findById(audit.getDriverId()).isPresent()) {
            throw new BusinessException("Motorista com ID " + audit.getDriverId() + " não encontrado.");
        }

        audit.setUpdatedAt(LocalDateTime.now());
        return complianceAuditDAO.update(audit);
    }

    @Override
    public boolean deleteAudit(int id) throws SQLException, BusinessException {
        if (!complianceAuditDAO.findById(id).isPresent()) {
            throw new BusinessException("Auditoria com ID " + id + " não encontrada para exclusão.");
        }
        return complianceAuditDAO.delete(id);
    }

    @Override
    public Optional<ComplianceAudit> getAuditById(int id) throws SQLException {
        return complianceAuditDAO.findById(id);
    }

    @Override
    public List<ComplianceAudit> getAllAudits() throws SQLException {
        return complianceAuditDAO.findAll();
    }

    @Override
    public List<ComplianceAudit> getAuditsByJourneyId(int journeyId) throws SQLException {
        return complianceAuditDAO.findByJourneyId(journeyId);
    }

    @Override
    public List<ComplianceAudit> getAuditsByDriverId(int driverId) throws SQLException {
        return complianceAuditDAO.findByDriverId(driverId);
    }

    @Override
    public List<ComplianceAudit> getAuditsByAuditDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        return complianceAuditDAO.findByAuditDateRange(startDate, endDate);
    }

    @Override
    public List<ComplianceAudit> getAuditsByStatus(ComplianceStatus status) throws SQLException {
        return complianceAuditDAO.findByStatus(status);
    }
}