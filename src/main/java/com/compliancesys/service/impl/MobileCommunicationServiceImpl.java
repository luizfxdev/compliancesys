package com.compliancesys.service.impl;

import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.service.MobileCommunicationService;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.ValidatorImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MobileCommunicationServiceImpl implements MobileCommunicationService {

    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationServiceImpl.class.getName());
    private final MobileCommunicationDAO mobileCommunicationDAO;
    private final Validator validator;

    public MobileCommunicationServiceImpl() {
        this.mobileCommunicationDAO = new MobileCommunicationDAOImpl();
        this.validator = new ValidatorImpl();
    }

    public MobileCommunicationServiceImpl(MobileCommunicationDAO mobileCommunicationDAO, Validator validator) {
        this.mobileCommunicationDAO = mobileCommunicationDAO;
        this.validator = validator;
    }

    @Override
    public MobileCommunication createMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException {
        if (communication == null) {
            throw new BusinessException("Comunicação móvel não pode ser nula.");
        }
        if (!validator.isValidDriverId(communication.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (!validator.isValidId(communication.getRecordId())) { // Usando isValidId para recordId
            throw new BusinessException("ID do registro de ponto inválido.");
        }
        if (communication.getTimestamp() == null) {
            throw new BusinessException("Timestamp da comunicação é obrigatório.");
        }
        if (communication.getLatitude() == null || communication.getLongitude() == null) {
            throw new BusinessException("Latitude e Longitude são obrigatórias.");
        }
        // Validações para sendTimestamp, sendSuccess, errorMessage
        if (communication.getSendTimestamp() == null) {
            throw new BusinessException("Timestamp de envio é obrigatório.");
        }
        if (communication.getErrorMessage() != null && communication.getErrorMessage().length() > 255) {
            throw new BusinessException("Mensagem de erro excede o limite de 255 caracteres.");
        }

        try {
            communication.setCreatedAt(LocalDateTime.now());
            communication.setUpdatedAt(LocalDateTime.now());
            int id = mobileCommunicationDAO.create(communication);
            if (id <= 0) {
                throw new BusinessException("Falha ao registrar comunicação móvel.");
            }
            communication.setId(id);
            LOGGER.log(Level.INFO, "Comunicação móvel registrada com sucesso. ID: {0}", id);
            return communication;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<MobileCommunication> getMobileCommunicationById(int id) throws BusinessException, SQLException {
        if (!validator.isValidId(id)) {
            throw new BusinessException("ID da comunicação móvel inválido.");
        }
        try {
            return mobileCommunicationDAO.findById(id);
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
    public List<MobileCommunication> getMobileCommunicationsByDriverId(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidDriverId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca.");
        }
        try {
            return mobileCommunicationDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por ID de motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getMobileCommunicationsByRecordId(int recordId) throws BusinessException, SQLException {
        if (!validator.isValidId(recordId)) { // Usando isValidId para recordId
            throw new BusinessException("ID do registro de ponto inválido para busca.");
        }
        try {
            return mobileCommunicationDAO.findByRecordId(recordId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por ID de registro: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar comunicações móveis. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public MobileCommunication updateMobileCommunication(MobileCommunication communication) throws BusinessException, SQLException {
        if (communication == null) {
            throw new BusinessException("Dados da comunicação móvel não podem ser nulos.");
        }
        if (!validator.isValidId(communication.getId())) {
            throw new BusinessException("ID da comunicação móvel inválido para atualização.");
        }
        if (!validator.isValidDriverId(communication.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (!validator.isValidId(communication.getRecordId())) { // Usando isValidId para recordId
            throw new BusinessException("ID do registro de ponto inválido.");
        }
        if (communication.getTimestamp() == null) {
            throw new BusinessException("Timestamp da comunicação é obrigatório.");
        }
        if (communication.getLatitude() == null || communication.getLongitude() == null) {
            throw new BusinessException("Latitude e Longitude são obrigatórias.");
        }
        // Validações para sendTimestamp, sendSuccess, errorMessage
        if (communication.getSendTimestamp() == null) {
            throw new BusinessException("Timestamp de envio é obrigatório.");
        }
        if (communication.getErrorMessage() != null && communication.getErrorMessage().length() > 255) {
            throw new BusinessException("Mensagem de erro excede o limite de 255 caracteres.");
        }

        try {
            communication.setUpdatedAt(LocalDateTime.now());
            boolean updated = mobileCommunicationDAO.update(communication);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar comunicação móvel. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Comunicação móvel atualizada com sucesso. ID: {0}", communication.getId());
            return communication;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteMobileCommunication(int id) throws BusinessException, SQLException {
        if (!validator.isValidId(id)) {
            throw new BusinessException("ID da comunicação móvel inválido para exclusão.");
        }
        try {
            boolean deleted = mobileCommunicationDAO.delete(id);
            if (!deleted) {
                throw new BusinessException("Comunicação móvel não encontrada para exclusão.");
            }
            LOGGER.log(Level.INFO, "Comunicação móvel deletada com sucesso. ID: {0}", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar comunicação móvel. Tente novamente mais tarde.", e);
        }
    }
}
