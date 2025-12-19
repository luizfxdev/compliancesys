package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.MobileCommunicationService;
import com.compliancesys.util.Validator;

public class MobileCommunicationServiceImpl implements MobileCommunicationService {
    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationServiceImpl.class.getName());

    private final MobileCommunicationDAO mobileCommunicationDAO;
    private final JourneyDAO journeyDAO;
    private final DriverDAO driverDAO;
    private final Validator validator;

    public MobileCommunicationServiceImpl(MobileCommunicationDAO mobileCommunicationDAO,
                                          JourneyDAO journeyDAO, DriverDAO driverDAO, Validator validator) {
        this.mobileCommunicationDAO = mobileCommunicationDAO;
        this.journeyDAO = journeyDAO;
        this.driverDAO = driverDAO;
        this.validator = validator;
    }

    @Override
    public MobileCommunication createMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException {
        if (communication == null) {
            throw new BusinessException("Dados da comunicação móvel não podem ser nulos.");
        }
        if (!validator.isValidId(communication.getJourneyId())) {
            throw new BusinessException("ID da jornada inválido.");
        }
        if (!validator.isValidId(communication.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (communication.getTimestamp() == null) {
            throw new BusinessException("Timestamp da comunicação é obrigatório.");
        }
        if (communication.getEventType() == null) {
            throw new BusinessException("Tipo de evento da comunicação é obrigatório.");
        }
        if (communication.getLatitude() == null || !validator.isValidLatitude(communication.getLatitude())) {
            throw new BusinessException("Latitude inválida.");
        }
        if (communication.getLongitude() == null || !validator.isValidLongitude(communication.getLongitude())) {
            throw new BusinessException("Longitude inválida.");
        }
        // Validações para signalStrength e batteryLevel, se houver regras de negócio específicas
        if (communication.getSignalStrength() < 0 || communication.getSignalStrength() > 100) {
            throw new BusinessException("Força do sinal deve estar entre 0 e 100.");
        }
        if (communication.getBatteryLevel() < 0 || communication.getBatteryLevel() > 100) {
            throw new BusinessException("Nível da bateria deve estar entre 0 e 100.");
        }

        try {
            if (!journeyDAO.findById(communication.getJourneyId()).isPresent()) {
                throw new BusinessException("Jornada com ID " + communication.getJourneyId() + " não encontrada.");
            }
            if (!driverDAO.findById(communication.getDriverId()).isPresent()) {
                throw new BusinessException("Motorista com ID " + communication.getDriverId() + " não encontrado.");
            }

            communication.setCreatedAt(LocalDateTime.now());
            communication.setUpdatedAt(LocalDateTime.now());
            int id = mobileCommunicationDAO.create(communication);
            communication.setId(id);
            LOGGER.log(Level.INFO, "Comunicação móvel registrada com sucesso: ID {0}", id);
            return communication;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<MobileCommunication> getMobileCommunicationById(int communicationId) throws BusinessException, SQLException {
        if (!validator.isValidId(communicationId)) {
            throw new BusinessException("ID da comunicação móvel inválido.");
        }
        try {
            return mobileCommunicationDAO.findById(communicationId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicação móvel por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getAllMobileCommunications() throws BusinessException, SQLException {
        try {
            return mobileCommunicationDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as comunicações móveis: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public MobileCommunication updateMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException {
        if (communication == null || !validator.isValidId(communication.getId())) {
            throw new BusinessException("Dados da comunicação móvel ou ID inválido para atualização.");
        }
        if (!validator.isValidId(communication.getJourneyId())) {
            throw new BusinessException("ID da jornada inválido.");
        }
        if (!validator.isValidId(communication.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (communication.getTimestamp() == null) {
            throw new BusinessException("Timestamp da comunicação é obrigatório.");
        }
        if (communication.getEventType() == null) {
            throw new BusinessException("Tipo de evento da comunicação é obrigatório.");
        }
        if (communication.getLatitude() == null || !validator.isValidLatitude(communication.getLatitude())) {
            throw new BusinessException("Latitude inválida.");
        }
        if (communication.getLongitude() == null || !validator.isValidLongitude(communication.getLongitude())) {
            throw new BusinessException("Longitude inválida.");
        }
        // Validações para signalStrength e batteryLevel, se houver regras de negócio específicas
        if (communication.getSignalStrength() < 0 || communication.getSignalStrength() > 100) {
            throw new BusinessException("Força do sinal deve estar entre 0 e 100.");
        }
        if (communication.getBatteryLevel() < 0 || communication.getBatteryLevel() > 100) {
            throw new BusinessException("Nível da bateria deve estar entre 0 e 100.");
        }

        try {
            Optional<MobileCommunication> existingCommunication = mobileCommunicationDAO.findById(communication.getId());
            if (!existingCommunication.isPresent()) {
                throw new BusinessException("Comunicação móvel com ID " + communication.getId() + " não encontrada para atualização.");
            }

            if (!journeyDAO.findById(communication.getJourneyId()).isPresent()) {
                throw new BusinessException("Jornada com ID " + communication.getJourneyId() + " não encontrada.");
            }
            if (!driverDAO.findById(communication.getDriverId()).isPresent()) {
                throw new BusinessException("Motorista com ID " + communication.getDriverId() + " não encontrado.");
            }

            communication.setCreatedAt(existingCommunication.get().getCreatedAt());
            communication.setUpdatedAt(LocalDateTime.now());
            boolean updated = mobileCommunicationDAO.update(communication);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar comunicação móvel. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Comunicação móvel atualizada com sucesso: ID {0}", communication.getId());
            return communication;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteMobileCommunication(int communicationId) throws BusinessException, SQLException {
        if (!validator.isValidId(communicationId)) {
            throw new BusinessException("ID da comunicação móvel inválido para exclusão.");
        }
        try {
            Optional<MobileCommunication> existingCommunication = mobileCommunicationDAO.findById(communicationId);
            if (!existingCommunication.isPresent()) {
                throw new BusinessException("Comunicação móvel com ID " + communicationId + " não encontrada para exclusão.");
            }
            boolean deleted = mobileCommunicationDAO.delete(communicationId);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar comunicação móvel. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Comunicação móvel deletada com sucesso. ID: {0}", communicationId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getMobileCommunicationsByJourneyId(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido para busca de comunicações móveis.");
        }
        try {
            return mobileCommunicationDAO.findByJourneyId(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por ID da jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis por jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getMobileCommunicationsByDriverId(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca de comunicações móveis.");
        }
        try {
            return mobileCommunicationDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis por motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getMobileCommunicationsByEventType(EventType eventType) throws BusinessException, SQLException {
        if (eventType == null) {
            throw new BusinessException("Tipo de evento inválido para busca de comunicações móveis.");
        }
        try {
            return mobileCommunicationDAO.findByEventType(eventType);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por tipo de evento: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis por tipo de evento. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getMobileCommunicationsByDateTimeRange(LocalDateTime start, LocalDateTime end) throws BusinessException, SQLException {
        if (start == null || end == null || start.isAfter(end)) {
            throw new BusinessException("Intervalo de datas/horas inválido para busca de comunicações móveis.");
        }
        try {
            return mobileCommunicationDAO.findByDateTimeRange(start, end);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por intervalo de datas/horas: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis por data/hora. Tente novamente mais tarde.", e);
        }
    }
}
