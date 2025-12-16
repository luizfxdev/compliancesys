package com.compliancesys.service.impl;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.ComplianceAuditService;
import com.compliancesys.util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

        Optional<Journey> existingJourney = journeyDAO.findById(audit.getJourneyId());
        if (!existingJourney.isPresent()) {
            throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada.");
        }

        Optional<Driver> existingDriver = driverDAO.findById(audit.getDriverId());
        if (!existingDriver.isPresent()) {
            throw new BusinessException("Motorista com ID " + audit.getDriverId() + " não encontrado.");
        }

        return complianceAuditDAO.create(audit);
    }

    @Override
    public boolean updateAudit(ComplianceAudit audit) throws SQLException, BusinessException {
        validator.validate(audit);

        Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(audit.getId());
        if (!existingAudit.isPresent()) {
            throw new BusinessException("Auditoria com ID " + audit.getId() + " não encontrada para atualização.");
        }

        Optional<Journey> existingJourney = journeyDAO.findById(audit.getJourneyId());
        if (!existingJourney.isPresent()) {
            throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada.");
        }

        Optional<Driver> existingDriver = driverDAO.findById(audit.getDriverId());
        if (!existingDriver.isPresent()) {
            throw new BusinessException("Motorista com ID " + audit.getDriverId() + " não encontrado.");
        }

        return complianceAuditDAO.update(audit);
    }

    @Override
    public boolean deleteAudit(int id) throws SQLException, BusinessException {
        Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(id);
        if (!existingAudit.isPresent()) {
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
