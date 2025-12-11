package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.model.report.ComplianceReport;
import com.compliancesys.service.ComplianceService;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class ComplianceServiceImpl implements ComplianceService {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServiceImpl.class.getName());

    private final ComplianceAuditDAO complianceAuditDAO;
    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO;
    private final DriverDAO driverDAO;
    private final Validator validator;
    private final TimeUtil timeUtil;

    public ComplianceServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO,
                                 TimeRecordDAO timeRecordDAO, DriverDAO driverDAO,
                                 Validator validator, TimeUtil timeUtil) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.driverDAO = driverDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    @Override
    public ComplianceAudit auditJourney(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido.");
        }

        Optional<Journey> journeyOptional = journeyDAO.findById(journeyId);
        if (!journeyOptional.isPresent()) {
            throw new BusinessException("Jornada não encontrada com o ID: " + journeyId);
        }
        Journey journey = journeyOptional.get();

        // Lógica de auditoria: verifica o status de conformidade da jornada
        String complianceStatus = journey.getComplianceStatus();

        ComplianceAudit audit = new ComplianceAudit();
        audit.setJourneyId(journeyId);
        audit.setAuditDate(LocalDateTime.now());
        audit.setComplianceStatus(complianceStatus);
        audit.setNotes("Auditoria automática");
        audit.setCreatedAt(LocalDateTime.now());
        audit.setUpdatedAt(LocalDateTime.now());

        int newId = complianceAuditDAO.create(audit);
        audit.setId(newId);

        LOGGER.log(Level.INFO, "Auditoria de conformidade realizada para a jornada ID {0} com status: {1}", 
                   new Object[]{journeyId, complianceStatus});
        return audit;
    }

    @Override
    public ComplianceReport generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) 
            throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (startDate == null || endDate == null) {
            throw new BusinessException("Datas de início e fim não podem ser nulas.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim.");
        }

        try {
            Optional<Driver> driverOptional = driverDAO.findById(driverId);
            if (!driverOptional.isPresent()) {
                throw new BusinessException("Motorista não encontrado com o ID: " + driverId);
            }
            Driver driver = driverOptional.get();

            List<ComplianceAudit> audits = complianceAuditDAO.findByDriverIdAndAuditDateRange(driverId, startDate, endDate);

            long totalAudits = audits.size();
            long compliantAudits = audits.stream()
                    .filter(audit -> ComplianceStatus.COMPLIANT.name().equals(audit.getComplianceStatus()) 
                            || ComplianceStatus.CONFORME.name().equals(audit.getComplianceStatus()))
                    .count();
            long nonCompliantAudits = audits.stream()
                    .filter(audit -> ComplianceStatus.NON_COMPLIANT.name().equals(audit.getComplianceStatus()) 
                            || ComplianceStatus.NAO_CONFORME.name().equals(audit.getComplianceStatus()))
                    .count();

            double complianceRate = (totalAudits > 0) ? ((double) compliantAudits / totalAudits) * 100 : 0.0;

            // Usar setters em vez de construtor
            ComplianceReport report = new ComplianceReport();
            report.setReportName("Relatório de Conformidade do Motorista");
            report.setGeneratedDate(LocalDate.now());
            report.setDriverId(driverId);
            report.setDriverName(driver.getName());
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setTotalAudits((int) totalAudits);
            report.setCompliantAudits((int) compliantAudits);
            report.setNonCompliantAudits((int) nonCompliantAudits);
            report.setComplianceRate(complianceRate);
            report.setAudits(audits);

            LOGGER.log(Level.INFO, "Relatório de conformidade gerado para motorista ID {0} de {1} a {2}.", 
                       new Object[]{driverId, startDate, endDate});
            return report;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório de conformidade para motorista ID " + driverId 
                       + ": " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao gerar relatório de conformidade.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAuditsByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) 
            throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (startDate == null || endDate == null) {
            throw new BusinessException("Datas de início e fim não podem ser nulas.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim.");
        }
        try {
            return complianceAuditDAO.findByDriverIdAndAuditDateRange(driverId, startDate, endDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias.", e);
        }
    }
}