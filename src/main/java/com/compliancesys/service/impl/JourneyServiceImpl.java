// src/main/java/com/compliancesys/service/impl/JourneyServiceImpl.java
package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime; // Adicionado para validação de jornada
import java.util.List; // Adicionado para validação de jornada
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO; // Importado para validação de jornada
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.JourneyService;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;

public class JourneyServiceImpl implements JourneyService {

    private static final Logger LOGGER = Logger.getLogger(JourneyServiceImpl.class.getName());

    private final JourneyDAO journeyDAO;
    private final DriverDAO driverDAO;
    private final VehicleDAO vehicleDAO;
    private final TimeRecordDAO timeRecordDAO; // Adicionado para verificar registros de tempo associados
    private final Validator validator;
    private final TimeUtil timeUtil; // Adicionado

    // Construtor ajustado para corresponder às dependências esperadas
    public JourneyServiceImpl(JourneyDAO journeyDAO, DriverDAO driverDAO, VehicleDAO vehicleDAO,
                              TimeRecordDAO timeRecordDAO, Validator validator, TimeUtil timeUtil) {
        this.journeyDAO = journeyDAO;
        this.driverDAO = driverDAO;
        this.vehicleDAO = vehicleDAO;
        this.timeRecordDAO = timeRecordDAO;
        this.validator = validator;
        this.timeUtil = timeUtil;
    }

    @Override
    public Journey createJourney(Journey journey) throws BusinessException, SQLException {
        if (journey == null) {
            throw new BusinessException("Dados da jornada não podem ser nulos.");
        }
        if (!validator.isValidId(journey.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (!validator.isValidId(journey.getVehicleId())) {
            throw new BusinessException("ID do veículo inválido.");
        }
        if (!validator.isValidId(journey.getCompanyId())) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada é obrigatória.");
        }
        if (journey.getStartLocation() == null || journey.getStartLocation().trim().isEmpty()) {
            throw new BusinessException("Local de início da jornada é obrigatório.");
        }

        try {
            // Validar se motorista, veículo e empresa existem
            if (!driverDAO.findById(journey.getDriverId()).isPresent()) {
                throw new BusinessException("Motorista com ID " + journey.getDriverId() + " não encontrado.");
            }
            if (!vehicleDAO.findById(journey.getVehicleId()).isPresent()) {
                throw new BusinessException("Veículo com ID " + journey.getVehicleId() + " não encontrado.");
            }
            // A validação da empresa pode ser feita no DriverService ou VehicleService, mas aqui é um bom lugar para garantir consistência
            // Se o DriverDAO e VehicleDAO já validam a existência da empresa, podemos remover esta linha.
            // Por enquanto, vamos assumir que a validação de companyId é feita em outro lugar ou que o ID é válido.

            // Verificar se já existe uma jornada para este motorista nesta data
            if (journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate()).isPresent()) {
                throw new BusinessException("Já existe uma jornada cadastrada para este motorista nesta data.");
            }

            journey.setCreatedAt(LocalDateTime.now());
            journey.setUpdatedAt(LocalDateTime.now());
            int id = journeyDAO.create(journey);
            if (id <= 0) {
                throw new BusinessException("Falha ao criar jornada. Tente novamente.");
            }
            journey.setId(id);
            LOGGER.log(Level.INFO, "Jornada criada com sucesso: ID {0} para motorista {1} em {2}",
                    new Object[]{journey.getId(), journey.getDriverId(), journey.getJourneyDate()});
            return journey;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao criar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyById(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido.");
        }
        try {
            return journeyDAO.findById(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getAllJourneys() throws BusinessException, SQLException {
        try {
            return journeyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as jornadas: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornadas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Journey updateJourney(Journey journey) throws BusinessException, SQLException {
        if (journey == null || !validator.isValidId(journey.getId())) {
            throw new BusinessException("Dados da jornada ou ID inválido para atualização.");
        }
        if (!validator.isValidId(journey.getDriverId())) {
            throw new BusinessException("ID do motorista inválido.");
        }
        if (!validator.isValidId(journey.getVehicleId())) {
            throw new BusinessException("ID do veículo inválido.");
        }
        if (!validator.isValidId(journey.getCompanyId())) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("Data da jornada é obrigatória.");
        }
        if (journey.getStartLocation() == null || journey.getStartLocation().trim().isEmpty()) {
            throw new BusinessException("Local de início da jornada é obrigatório.");
        }

        try {
            Optional<Journey> existingJourney = journeyDAO.findById(journey.getId());
            if (!existingJourney.isPresent()) {
                throw new BusinessException("Jornada com ID " + journey.getId() + " não encontrada para atualização.");
            }

            // Validar se motorista, veículo e empresa existem
            if (!driverDAO.findById(journey.getDriverId()).isPresent()) {
                throw new BusinessException("Motorista com ID " + journey.getDriverId() + " não encontrado.");
            }
            if (!vehicleDAO.findById(journey.getVehicleId()).isPresent()) {
                throw new BusinessException("Veículo com ID " + journey.getVehicleId() + " não encontrado.");
            }

            // Verificar se já existe outra jornada para este motorista nesta data (excluindo a própria jornada)
            Optional<Journey> journeyByDriverAndDate = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
            if (journeyByDriverAndDate.isPresent() && journeyByDriverAndDate.get().getId() != journey.getId()) {
                throw new BusinessException("Já existe outra jornada cadastrada para este motorista nesta data.");
            }

            journey.setCreatedAt(existingJourney.get().getCreatedAt()); // Mantém a data de criação original
            journey.setUpdatedAt(LocalDateTime.now());
            boolean updated = journeyDAO.update(journey);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar jornada. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Jornada atualizada com sucesso: ID {0}", journey.getId());
            return journey;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteJourney(int journeyId) throws BusinessException, SQLException {
        if (!validator.isValidId(journeyId)) {
            throw new BusinessException("ID da jornada inválido para exclusão.");
        }
        try {
            Optional<Journey> existingJourney = journeyDAO.findById(journeyId);
            if (!existingJourney.isPresent()) {
                throw new BusinessException("Jornada com ID " + journeyId + " não encontrada para exclusão.");
            }

            // Antes de deletar a jornada, verificar se há registros de tempo associados
            List<TimeRecord> associatedTimeRecords = timeRecordDAO.findByJourneyId(journeyId);
            if (!associatedTimeRecords.isEmpty()) {
                throw new BusinessException("Não é possível deletar a jornada com ID " + journeyId + " pois existem registros de tempo associados. Delete os registros de tempo primeiro.");
            }

            boolean deleted = journeyDAO.delete(journeyId);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar jornada. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Jornada deletada com sucesso. ID: {0}", journeyId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByDriverId(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca de jornadas.");
        }
        try {
            return journeyDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por ID do motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornadas por motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByVehicleId(int vehicleId) throws BusinessException, SQLException {
        if (!validator.isValidId(vehicleId)) {
            throw new BusinessException("ID do veículo inválido para busca de jornadas.");
        }
        try {
            return journeyDAO.findByVehicleId(vehicleId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por ID do veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornadas por veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByCompanyId(int companyId) throws BusinessException, SQLException {
        if (!validator.isValidId(companyId)) {
            throw new BusinessException("ID da empresa inválido para busca de jornadas.");
        }
        try {
            return journeyDAO.findByCompanyId(companyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por ID da empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornadas por empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByDateRange(LocalDate startDate, LocalDate endDate) throws BusinessException, SQLException {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessException("Intervalo de datas inválido para busca de jornadas.");
        }
        try {
            return journeyDAO.findByDateRange(startDate, endDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por intervalo de datas: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornadas por intervalo de datas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para busca de jornada.");
        }
        if (journeyDate == null) {
            throw new BusinessException("Data da jornada é obrigatória para busca.");
        }
        try {
            return journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar jornada por motorista e data. Tente novamente mais tarde.", e);
        }
    }
}
