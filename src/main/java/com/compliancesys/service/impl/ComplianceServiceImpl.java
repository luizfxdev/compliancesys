package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List; // Importar o enum
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceReport;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.ComplianceService;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.ValidatorImpl;

public class ComplianceServiceImpl implements ComplianceService {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServiceImpl.class.getName());

    private final ComplianceAuditDAO complianceAuditDAO;
    private final CompanyDAO companyDAO; // Pode ser null se não injetado
    private final DriverDAO driverDAO;   // Pode ser null se não injetado
    private final VehicleDAO vehicleDAO; // Pode ser null se não injetado
    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO; // Pode ser null se não injetado
    private final Validator validator;

    // Construtor padrão para instanciar DAOs e Validator
    public ComplianceServiceImpl() {
        this.complianceAuditDAO = new ComplianceAuditDAOImpl();
        this.companyDAO = new CompanyDAOImpl();
        this.driverDAO = new DriverDAOImpl();
        this.vehicleDAO = new VehicleDAOImpl();
        this.journeyDAO = new JourneyDAOImpl();
        this.timeRecordDAO = new TimeRecordDAOImpl();
        this.validator = new ValidatorImpl();
    }

    // Construtor para injeção de dependência (para testes ou frameworks)
    public ComplianceServiceImpl(ComplianceAuditDAO complianceAuditDAO, CompanyDAO companyDAO,
                                 DriverDAO driverDAO, VehicleDAO vehicleDAO, JourneyDAO journeyDAO,
                                 TimeRecordDAO timeRecordDAO, Validator validator) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.companyDAO = companyDAO;
        this.driverDAO = driverDAO;
        this.vehicleDAO = vehicleDAO;
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.validator = validator;
    }

    // NOVO CONSTRUTOR PARA ATENDER A CHAMADA DO ComplianceServlet
    // Este construtor inicializa os DAOs que não são passados diretamente
    public ComplianceServiceImpl(ComplianceAuditDAO complianceAuditDAO, JourneyDAO journeyDAO, Validator validator) {
        this.complianceAuditDAO = complianceAuditDAO;
        this.journeyDAO = journeyDAO;
        this.validator = validator;
        // Inicializa os outros DAOs que não são passados, se forem necessários para outros métodos
        this.companyDAO = new CompanyDAOImpl();
        this.driverDAO = new DriverDAOImpl();
        this.vehicleDAO = new VehicleDAOImpl();
        this.timeRecordDAO = new TimeRecordDAOImpl();
    }

    @Override
    public ComplianceAudit createComplianceAudit(ComplianceAudit audit) throws BusinessException {
        if (audit == null) {
            throw new BusinessException("Auditoria de conformidade não pode ser nula.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("ID da jornada é obrigatório e deve ser um valor positivo.");
        }
        // audit.getAuditDate() é LocalDateTime, então a validação abaixo é mais apropriada
        if (audit.getAuditDate() == null) {
            throw new BusinessException("Data da auditoria é obrigatória.");
        }
        if (audit.getComplianceStatus() == null) {
            throw new BusinessException("Status de conformidade é obrigatório.");
        }

        try {
            // Verifica se a jornada associada existe
            Optional<Journey> existingJourney = journeyDAO.findById(audit.getJourneyId());
            if (!existingJourney.isPresent()) {
                throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada. Não é possível criar a auditoria.");
            }

            audit.setCreatedAt(LocalDateTime.now());
            audit.setUpdatedAt(LocalDateTime.now());
            int newId = complianceAuditDAO.create(audit);
            audit.setId(newId);
            LOGGER.log(Level.INFO, "Auditoria de conformidade criada com sucesso: ID {0}", newId);
            return audit;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar auditoria de conformidade.", e);
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
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditoria por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditoria por ID.", e);
        }
    }

    @Override
    public List<ComplianceAudit> getAllComplianceAudits() throws BusinessException {
        try {
            return complianceAuditDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as auditorias: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar todas as auditorias.", e);
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
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar auditorias por ID da jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar auditorias por ID da jornada.", e);
        }
    }

    @Override
    public ComplianceAudit updateComplianceAudit(ComplianceAudit audit) throws BusinessException {
        if (audit == null || audit.getId() <= 0) {
            throw new BusinessException("Auditoria de conformidade ou ID inválido para atualização.");
        }
        if (audit.getJourneyId() <= 0) {
            throw new BusinessException("ID da jornada é obrigatório e deve ser um valor positivo.");
        }
        if (audit.getAuditDate() == null) {
            throw new BusinessException("Data da auditoria é obrigatória.");
        }
        if (audit.getComplianceStatus() == null) {
            throw new BusinessException("Status de conformidade é obrigatório.");
        }

        try {
            // Verifica se a auditoria existe
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(audit.getId());
            if (!existingAudit.isPresent()) {
                throw new BusinessException("Auditoria com ID " + audit.getId() + " não encontrada.");
            }
            // Verifica se a jornada associada existe
            Optional<Journey> existingJourney = journeyDAO.findById(audit.getJourneyId());
            if (!existingJourney.isPresent()) {
                throw new BusinessException("Jornada com ID " + audit.getJourneyId() + " não encontrada. Não é possível atualizar a auditoria.");
            }

            audit.setUpdatedAt(LocalDateTime.now());
            boolean updated = complianceAuditDAO.update(audit);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar a auditoria de conformidade com ID " + audit.getId() + ".");
            }
            LOGGER.log(Level.INFO, "Auditoria de conformidade atualizada com sucesso: ID {0}", audit.getId());
            return audit; // Retorna o objeto atualizado
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar auditoria de conformidade.", e);
        }
    }

    @Override
    public boolean deleteComplianceAudit(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da auditoria inválido para exclusão.");
        }
        try {
            // Verifica se a auditoria existe antes de tentar deletar
            Optional<ComplianceAudit> existingAudit = complianceAuditDAO.findById(id);
            if (!existingAudit.isPresent()) {
                throw new BusinessException("Auditoria com ID " + id + " não encontrada para exclusão.");
            }
            boolean deleted = complianceAuditDAO.delete(id);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar a auditoria de conformidade com ID " + id + ".");
            }
            LOGGER.log(Level.INFO, "Auditoria de conformidade deletada com sucesso: ID {0}", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar auditoria de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar auditoria de conformidade.", e);
        }
    }

    @Override
    public ComplianceAudit performComplianceAudit(int journeyId) throws BusinessException {
        if (journeyId <= 0) {
            throw new BusinessException("ID da jornada inválido para realizar auditoria.");
        }
        try {
            Optional<Journey> journeyOpt = journeyDAO.findById(journeyId);
            if (!journeyOpt.isPresent()) {
                throw new BusinessException("Jornada com ID " + journeyId + " não encontrada.");
            }
            Journey journey = journeyOpt.get();

            // Lógica de auditoria de conformidade (simplificada para exemplo)
            // Em um cenário real, esta lógica seria mais complexa, envolvendo TimeRecords, regras de negócio, etc.
            ComplianceStatus status = ComplianceStatus.COMPLIANT; // Assumindo conforme por padrão
            String notes = "Auditoria básica realizada. Status inicial: CONFORME."; // Usando 'notes' em vez de 'details'

            // Exemplo: Se a jornada durou mais de 10 horas, é não conforme
            // if (journey.getStartTime() != null && journey.getEndTime() != null) {
            //      Duration duration = Duration.between(journey.getStartTime(), journey.getEndTime());
            //      if (duration.toHours() > 10) {
            //          status = ComplianceStatus.NON_COMPLIANT;
            //          notes = "Jornada excedeu 10 horas de duração.";
            //      }
            // }

            // Cria ou atualiza o registro de auditoria
            ComplianceAudit audit = new ComplianceAudit();
            audit.setJourneyId(journeyId);
            audit.setAuditDate(LocalDateTime.now());
            audit.setComplianceStatus(status);
            audit.setNotes(notes); // CORREÇÃO AQUI: Usar setNotes()
            audit.setAuditorName("Sistema Automático"); // Ou o nome do usuário logado

            // Tenta encontrar uma auditoria existente para esta jornada
            List<ComplianceAudit> existingAudits = complianceAuditDAO.findByJourneyId(journeyId);
            if (!existingAudits.isEmpty()) {
                // Se existir, atualiza a mais recente (ou a que se encaixa melhor na lógica de negócio)
                // Para simplificar, vamos atualizar a primeira encontrada
                ComplianceAudit latestAudit = existingAudits.get(0);
                audit.setId(latestAudit.getId());
                audit.setCreatedAt(latestAudit.getCreatedAt()); // Mantém o original
                return updateComplianceAudit(audit); // Reutiliza o método de atualização
            } else {
                // Se não existir, cria uma nova
                return createComplianceAudit(audit); // Reutiliza o método de criação
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao realizar auditoria de conformidade para jornada {0}: {1}", new Object[]{journeyId, e.getMessage()});
            throw new BusinessException("Erro interno ao realizar auditoria de conformidade.", e);
        }
    }

    @Override
    public ComplianceReport generateDriverComplianceReport(int driverId, LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (startDate == null || endDate == null) {
            throw new BusinessException("Datas de início e fim são obrigatórias.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("A data de início não pode ser posterior à data de fim.");
        }

        try {
            // driverDAO pode ser null se o construtor usado não o injetar.
            // É importante garantir que ele seja inicializado ou que a lógica lide com null.
            // No construtor padrão, ele é inicializado. No construtor de 3 parâmetros, ele não é.
            // Se este método for chamado por um serviço que usa o construtor de 3 parâmetros, driverDAO será null.
            // Para este exemplo, vou assumir que driverDAO está inicializado.
            if (driverDAO == null) {
                throw new BusinessException("DriverDAO não inicializado. Não é possível gerar relatório de motorista.");
            }

            Optional<Driver> driverOpt = driverDAO.findById(driverId);
            if (!driverOpt.isPresent()) {
                throw new BusinessException("Motorista com ID " + driverId + " não encontrado.");
            }
            Driver driver = driverOpt.get();

            // Buscar todas as jornadas do motorista no período
            // journeyDAO pode ser null se o construtor usado não o injetar.
            if (journeyDAO == null) {
                throw new BusinessException("JourneyDAO não inicializado. Não é possível gerar relatório de motorista.");
            }
            List<Journey> driverJourneys = journeyDAO.findByDriverIdAndDateRange(driverId, startDate, endDate);

            int totalAudits = 0;
            int compliantAudits = 0;
            List<ComplianceAudit> audits = new ArrayList<>();

            for (Journey journey : driverJourneys) {
                // Para cada jornada, buscar as auditorias associadas
                List<ComplianceAudit> journeyAudits = complianceAuditDAO.findByJourneyId(journey.getId());
                for (ComplianceAudit audit : journeyAudits) {
                    // Filtrar auditorias dentro do período especificado
                    // A data da auditoria é LocalDateTime, então convertemos para LocalDate para comparação
                    if (!audit.getAuditDate().toLocalDate().isBefore(startDate) && !audit.getAuditDate().toLocalDate().isAfter(endDate)) {
                        audits.add(audit);
                        totalAudits++;
                        if (audit.getComplianceStatus() == ComplianceStatus.COMPLIANT) { // CORRIGIDO: Usando getComplianceStatus()
                            compliantAudits++;
                        }
                    }
                }
            }

            double complianceRate = (totalAudits > 0) ? ((double) compliantAudits / totalAudits) * 100 : 0.0;

            ComplianceReport report = new ComplianceReport();
            // CORREÇÃO AQUI: Preenchendo os setters da ComplianceReport
            report.setReportName("Relatório de Conformidade do Motorista");
            report.setGeneratedDate(LocalDate.now());
            report.setDriverId(driverId);
            report.setDriverName(driver.getName());
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setTotalAudits(totalAudits);
            report.setCompliantAudits(compliantAudits);
            report.setNonCompliantAudits(totalAudits - compliantAudits);
            report.setComplianceRate(complianceRate);
            report.setAudits(audits); // Adiciona as auditorias detalhadas ao relatório

            LOGGER.log(Level.INFO, "Relatório de conformidade do motorista {0} gerado para o período de {1} a {2}.",
                                    new Object[]{driver.getName(), startDate, endDate});
            return report;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório de conformidade do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao gerar relatório de conformidade do motorista.", e);
        }
    }

    @Override
    public List<ComplianceAudit> generateOverallComplianceReport(LocalDate startDate, LocalDate endDate) throws BusinessException {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Datas de início e fim são obrigatórias.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("A data de início não pode ser posterior à data de fim.");
        }
        try {
            // Assumindo que o ComplianceAuditDAO tem um método findByAuditDateRange
            List<ComplianceAudit> audits = complianceAuditDAO.findByAuditDateRange(startDate, endDate);
            LOGGER.log(Level.INFO, "Relatório geral de conformidade gerado para o período de {0} a {1}. Total de auditorias: {2}",
                                    new Object[]{startDate, endDate, audits.size()});
            return audits;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao gerar relatório geral de conformidade: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao gerar relatório geral de conformidade.", e);
        }
    }
}
