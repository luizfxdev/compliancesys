package com.compliancesys.service.impl; // CORRIGIDO: Pacote para impl

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.ComplianceStatus; // Importado para usar o enum
import com.compliancesys.service.JourneyService; // Importado para a interface
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class JourneyServiceImpl implements JourneyService {

    private static final Logger LOGGER = Logger.getLogger(JourneyServiceImpl.class.getName());

    private final JourneyDAO journeyDAO;
    private final TimeRecordDAO timeRecordDAO;
    private final DriverDAO driverDAO;
    private final Validator validator;
    private final TimeUtil timeUtil;

    // Construtor ajustado para corresponder à chamada do JourneyServlet
    public JourneyServiceImpl(JourneyDAO journeyDAO, TimeRecordDAO timeRecordDAO, DriverDAO driverDAO, Validator validator, TimeUtil timeUtil) {
        this.journeyDAO = journeyDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.driverDAO = driverDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    @Override
    public Journey createJourney(Journey journey) throws BusinessException, SQLException {
        if (journey == null) {
            throw new BusinessException("Jornada não pode ser nula.");
        }
        if (!validator.isValidId(journey.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        // Outras validações...

        int newId = journeyDAO.create(journey);
        journey.setId(newId);
        LOGGER.log(Level.INFO, "Jornada criada com ID: {0}", newId);
        return journey;
    }

    @Override
    public Optional<Journey> getJourneyById(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido.");
        }
        return journeyDAO.findById(journeyId);
    }

    @Override
    public List<Journey> getAllJourneys() throws BusinessException, SQLException {
        return journeyDAO.findAll();
    }

    @Override
    public List<Journey> getJourneysByDriverId(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        return journeyDAO.findByDriverId(driverId);
    }

    @Override
    public Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (journeyDate == null) {
            throw new BusinessException("Data da jornada não pode ser nula.");
        }
        return journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
    }

    @Override
    public Journey updateJourney(Journey journey) throws BusinessException, SQLException {
        if (journey == null) {
            throw new BusinessException("Jornada não pode ser nula.");
        }
        if (!validator.isValidId(journey.getId())) {
            throw new BusinessException("ID da jornada inválido para atualização.");
        }
        // Outras validações...

        journeyDAO.update(journey);
        LOGGER.log(Level.INFO, "Jornada ID {0} atualizada.", journey.getId());
        return journey;
    }

    @Override
    public boolean deleteJourney(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido para exclusão.");
        }
        boolean deleted = journeyDAO.delete(journeyId);
        if (deleted) {
            LOGGER.log(Level.INFO, "Jornada ID {0} deletada.", journeyId);
        } else {
            LOGGER.log(Level.WARNING, "Jornada ID {0} não encontrada para exclusão.", journeyId);
        }
        return deleted;
    }

    @Override
    public Journey calculateAndAuditJourney(int driverId, List<TimeRecord> timeRecords) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (timeRecords == null || timeRecords.isEmpty()) {
            throw new BusinessException("Registros de ponto não podem ser nulos ou vazios para calcular a jornada.");
        }

        // 1. Encontrar ou criar a jornada para a data dos registros de ponto
        LocalDate journeyDate = timeRecords.get(0).getRecordTime().toLocalDate();
        Optional<Journey> existingJourney = journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
        Journey journey;

        if (existingJourney.isPresent()) {
            journey = existingJourney.get();
        } else {
            journey = new Journey();
            journey.setDriverId(driverId);
            journey.setJourneyDate(journeyDate);
            journey.setCreatedAt(LocalDateTime.now());
            journey.setUpdatedAt(LocalDateTime.now());
            journey.setComplianceStatus(ComplianceStatus.PENDING.name()); // Define um status inicial
            journey.setTotalDrivingTimeMinutes(0);
            journey.setTotalRestTimeMinutes(0);
            journey.setDailyLimitExceeded(false);
            journey.setId(journeyDAO.create(journey)); // Cria a jornada e obtém o ID
        }

        // 2. Calcular tempos de condução e descanso
        long totalDrivingMinutes = timeUtil.calculateTotalDrivingTime(timeRecords);
        long totalRestMinutes = timeUtil.calculateTotalRestTime(timeRecords);

        journey.setTotalDrivingTimeMinutes((int) totalDrivingMinutes);
        journey.setTotalRestTimeMinutes((int) totalRestMinutes);

        // 3. Avaliar conformidade (exemplo simplificado)
        // Lei do Motorista (Lei 13.103/2015) - Exemplo de regras:
        // - Máximo de 8h de condução em 24h (prorrogáveis por mais 2h)
        // - Mínimo de 11h de descanso a cada 24h (podendo ser fracionado)
        // - Mínimo de 30min de descanso a cada 5h30 de condução

        boolean dailyLimitExceeded = false;
        String complianceStatus = ComplianceStatus.CONFORME.name();
        // String notes = ""; // Journey não tem campo notes, este campo é do ComplianceAudit

        if (totalDrivingMinutes > (8 * 60) + (2 * 60)) { // Mais de 10h de condução (8h + 2h extra)
            dailyLimitExceeded = true;
            complianceStatus = ComplianceStatus.NAO_CONFORME.name();
            // notes += "Limite diário de condução excedido. ";
        } else if (totalDrivingMinutes > (8 * 60)) { // Mais de 8h, mas dentro das 10h permitidas com prorrogação
            complianceStatus = ComplianceStatus.ALERTA.name();
            // notes += "Condução estendida além do limite regular de 8h. ";
        }

        // Exemplo de verificação de descanso (simplificado)
        if (totalRestMinutes < (11 * 60)) { // Menos de 11h de descanso
            if (complianceStatus.equals(ComplianceStatus.CONFORME.name())) {
                complianceStatus = ComplianceStatus.ALERTA.name();
            } else {
                complianceStatus = ComplianceStatus.NAO_CONFORME.name();
            }
            // notes += "Descanso diário insuficiente. ";
        }

        journey.setDailyLimitExceeded(dailyLimitExceeded);
        journey.setComplianceStatus(complianceStatus);
        journey.setUpdatedAt(LocalDateTime.now());

        journeyDAO.update(journey); // Atualiza a jornada no banco de dados

        LOGGER.log(Level.INFO, "Jornada ID {0} auditada. Status: {1}", new Object[]{journey.getId(), complianceStatus});
        return journey;
    }
}
