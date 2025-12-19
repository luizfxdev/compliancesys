package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;
import com.compliancesys.util.Validator;

public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOGGER = Logger.getLogger(VehicleServiceImpl.class.getName());
    private final VehicleDAO vehicleDAO;
    private final CompanyDAO companyDAO;
    private final Validator validator;

    public VehicleServiceImpl(VehicleDAO vehicleDAO, CompanyDAO companyDAO, Validator validator) {
        this.vehicleDAO = vehicleDAO;
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) throws BusinessException, SQLException {
        validator.validate(vehicle);

        if (vehicleDAO.findByPlate(vehicle.getPlate()).isPresent()) {
            throw new BusinessException("Já existe um veículo com a placa informada.");
        }
        if (!companyDAO.findById(vehicle.getCompanyId()).isPresent()) {
            throw new BusinessException("Empresa associada ao veículo não encontrada.");
        }

        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);
        
        int id = vehicleDAO.create(vehicle);
        vehicle.setId(id);
        return vehicle;
    }

    @Override
    public Vehicle updateVehicle(Vehicle vehicle) throws BusinessException, SQLException {
        validator.validate(vehicle);

        if (!validator.isValidId(vehicle.getId())) {
            throw new BusinessException("ID do veículo inválido para atualização.");
        }

        Optional<Vehicle> existingVehicle = vehicleDAO.findById(vehicle.getId());
        if (!existingVehicle.isPresent()) {
            throw new BusinessException("Veículo não encontrado para atualização.");
        }

        Optional<Vehicle> vehicleWithSamePlate = vehicleDAO.findByPlate(vehicle.getPlate());
        if (vehicleWithSamePlate.isPresent() && vehicleWithSamePlate.get().getId() != vehicle.getId()) {
            throw new BusinessException("Já existe outro veículo com a placa informada.");
        }

        if (!companyDAO.findById(vehicle.getCompanyId()).isPresent()) {
            throw new BusinessException("Empresa associada ao veículo não encontrada.");
        }

        vehicle.setCreatedAt(existingVehicle.get().getCreatedAt());
        vehicle.setUpdatedAt(LocalDateTime.now());
        
        if (!vehicleDAO.update(vehicle)) {
            throw new BusinessException("Falha ao atualizar veículo.");
        }
        return vehicle;
    }

    @Override
    public boolean deleteVehicle(int id) throws BusinessException, SQLException {
        if (!validator.isValidId(id)) {
            throw new BusinessException("ID do veículo inválido para exclusão.");
        }
        return vehicleDAO.delete(id);
    }

    @Override
    public Optional<Vehicle> getVehicleById(int id) throws SQLException {
        if (!validator.isValidId(id)) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar veículo com ID inválido: {0}", id);
            return Optional.empty();
        }
        return vehicleDAO.findById(id);
    }

    @Override
    public Optional<Vehicle> getVehicleByPlate(String plate) throws SQLException {
        if (plate == null || plate.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar veículo com placa nula ou vazia.");
            return Optional.empty();
        }
        return vehicleDAO.findByPlate(plate);
    }

    @Override
    public List<Vehicle> getAllVehicles() throws SQLException {
        return vehicleDAO.findAll();
    }

    @Override
    public List<Vehicle> getVehiclesByCompanyId(int companyId) throws SQLException {
        if (!validator.isValidId(companyId)) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar veículos com ID de empresa inválido: {0}", companyId);
            return new ArrayList<>();
        }
        return vehicleDAO.findByCompanyId(companyId);
    }

    @Override
    public List<Vehicle> getVehiclesByModel(String model) throws SQLException {
        if (model == null || model.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar veículos com modelo nulo ou vazio.");
            return new ArrayList<>();
        }
        return vehicleDAO.findByModel(model);
    }

    @Override
    public List<Vehicle> getVehiclesByYear(int year) throws SQLException {
        if (year <= 1900 || year > LocalDateTime.now().getYear() + 1) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar veículos com ano inválido: {0}", year);
            return new ArrayList<>();
        }
        return vehicleDAO.findByYear(year);
    }
}