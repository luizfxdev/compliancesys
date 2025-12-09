package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException; // Import adicionado
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;
import com.compliancesys.util.Validator;

public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOGGER = Logger.getLogger(VehicleServiceImpl.class.getName());
    private final VehicleDAO vehicleDAO;
    private final Validator validator;

    public VehicleServiceImpl(VehicleDAO vehicleDAO, Validator validator) {
        this.vehicleDAO = vehicleDAO;
        this.validator = validator;
    }

    @Override
    public Vehicle registerVehicle(Vehicle vehicle) throws BusinessException, SQLException {
        if (vehicle == null) {
            throw new BusinessException("Veículo não pode ser nulo.");
        }
        if (vehicle.getCompanyId() <= 0) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (!validator.isValidPlate(vehicle.getPlate())) {
            throw new BusinessException("Placa do veículo inválida.");
        }
        if (vehicle.getManufacturer() == null || vehicle.getManufacturer().trim().isEmpty()) {
            throw new BusinessException("Fabricante do veículo é obrigatório.");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new BusinessException("Modelo do veículo é obrigatório.");
        }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDateTime.now().getYear() + 1) {
            throw new BusinessException("Ano do veículo inválido.");
        }

        try {
            if (vehicleDAO.findByPlate(vehicle.getPlate()).isPresent()) {
                throw new BusinessException("Já existe um veículo cadastrado com esta placa.");
            }

            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUpdatedAt(LocalDateTime.now());
            int id = vehicleDAO.create(vehicle);
            vehicle.setId(id);
            LOGGER.log(Level.INFO, "Veículo registrado com sucesso: ID {0}", id);
            return vehicle;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao registrar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Vehicle> getVehicleById(int vehicleId) throws BusinessException, SQLException {
        if (vehicleId <= 0) {
            throw new BusinessException("ID do veículo inválido.");
        }
        try {
            return vehicleDAO.findById(vehicleId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículo por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Vehicle> getVehicleByPlate(String plate) throws BusinessException, SQLException {
        if (!validator.isValidPlate(plate)) {
            throw new BusinessException("Placa inválida para busca.");
        }
        try {
            return vehicleDAO.findByPlate(plate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículo por placa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() throws BusinessException, SQLException {
        try {
            return vehicleDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os veículos: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar veículos. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) throws BusinessException, SQLException { // Retorna boolean
        if (vehicle == null || vehicle.getId() <= 0) {
            throw new BusinessException("Veículo ou ID inválido para atualização.");
        }
        if (vehicle.getCompanyId() <= 0) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (!validator.isValidPlate(vehicle.getPlate())) {
            throw new BusinessException("Placa do veículo inválida.");
        }
        if (vehicle.getManufacturer() == null || vehicle.getManufacturer().trim().isEmpty()) {
            throw new BusinessException("Fabricante do veículo é obrigatório.");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new BusinessException("Modelo do veículo é obrigatório.");
        }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDateTime.now().getYear() + 1) {
            throw new BusinessException("Ano do veículo inválido.");
        }

        try {
            Optional<Vehicle> existingVehicle = vehicleDAO.findById(vehicle.getId());
            if (existingVehicle.isEmpty()) {
                throw new BusinessException("Veículo com ID " + vehicle.getId() + " não encontrado para atualização.");
            }

            if (!existingVehicle.get().getPlate().equals(vehicle.getPlate())) {
                if (vehicleDAO.findByPlate(vehicle.getPlate()).isPresent()) {
                    throw new BusinessException("Já existe outro veículo cadastrado com a placa informada.");
                }
            }

            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicle.setCreatedAt(existingVehicle.get().getCreatedAt());

            boolean updated = vehicleDAO.update(vehicle);
            if (updated) {
                LOGGER.log(Level.INFO, "Veículo atualizado com sucesso: ID {0}", vehicle.getId());
            } else {
                LOGGER.log(Level.WARNING, "Falha ao atualizar veículo. Nenhuma linha afetada.");
            }
            return updated; // Retorna boolean
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteVehicle(int vehicleId) throws BusinessException, SQLException {
        if (vehicleId <= 0) {
            throw new BusinessException("ID do veículo inválido para exclusão.");
        }
        try {
            Optional<Vehicle> existingVehicle = vehicleDAO.findById(vehicleId);
            if (existingVehicle.isEmpty()) {
                throw new BusinessException("Veículo com ID " + vehicleId + " não encontrado para exclusão.");
            }
            boolean deleted = vehicleDAO.delete(vehicleId);
            if (deleted) {
                LOGGER.log(Level.INFO, "Veículo com ID {0} deletado com sucesso.", vehicleId);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar veículo com ID {0}.", vehicleId);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar veículo. Tente novamente mais tarde.", e);
        }
    }
}
