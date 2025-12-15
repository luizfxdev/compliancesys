// src/main/java/com/compliancesys/service/impl/VehicleServiceImpl.java
package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO; // Adicionado para validação
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;
import com.compliancesys.util.Validator;
// import com.compliancesys.util.impl.ValidatorImpl; // Não é necessário importar a implementação

public class VehicleServiceImpl implements VehicleService {
    private static final Logger LOGGER = Logger.getLogger(VehicleServiceImpl.class.getName());

    private final VehicleDAO vehicleDAO;
    private final CompanyDAO companyDAO; // Adicionado para validar a empresa
    private final Validator validator;

    // Construtor para injeção de dependência
    public VehicleServiceImpl(VehicleDAO vehicleDAO, CompanyDAO companyDAO, Validator validator) {
        this.vehicleDAO = vehicleDAO;
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    // REMOVIDO: Construtor padrão que instanciaria DAOs sem Connection
    // public VehicleServiceImpl() {
    //     this.vehicleDAO = new VehicleDAOImpl();
    //     this.validator = new ValidatorImpl();
    // }

    @Override
    public Vehicle registerVehicle(Vehicle vehicle) throws BusinessException, SQLException {
        if (vehicle == null) {
            throw new BusinessException("Dados do veículo não podem ser nulos.");
        }
        if (!validator.isValidId(vehicle.getCompanyId())) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (!validator.isValidPlate(vehicle.getPlate())) {
            throw new BusinessException("Placa do veículo inválida.");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new BusinessException("Modelo do veículo é obrigatório.");
        }
        // REMOVIDO: Validação de Manufacturer, pois não existe no modelo Vehicle atual
        // if (vehicle.getManufacturer() == null || vehicle.getManufacturer().trim().isEmpty()) {
        //     throw new BusinessException("Fabricante do veículo é obrigatório.");
        // }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDateTime.now().getYear() + 1) {
            throw new BusinessException("Ano do veículo inválido. Deve ser entre 1900 e o ano atual + 1.");
        }

        try {
            // Validar se a empresa existe
            if (!companyDAO.findById(vehicle.getCompanyId()).isPresent()) {
                throw new BusinessException("Empresa com ID " + vehicle.getCompanyId() + " não encontrada.");
            }

            if (vehicleDAO.findByPlate(vehicle.getPlate()).isPresent()) {
                throw new BusinessException("Já existe um veículo cadastrado com esta placa.");
            }

            vehicle.setCreatedAt(LocalDateTime.now());
            vehicle.setUpdatedAt(LocalDateTime.now());
            int id = vehicleDAO.create(vehicle);
            if (id <= 0) {
                throw new BusinessException("Falha ao registrar veículo. Tente novamente.");
            }
            vehicle.setId(id);
            LOGGER.log(Level.INFO, "Veículo registrado com sucesso: ID {0}, Placa: {1}", new Object[]{id, vehicle.getPlate()});
            return vehicle;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Vehicle> getVehicleById(int id) throws BusinessException, SQLException {
        if (!validator.isValidId(id)) {
            throw new BusinessException("ID do veículo inválido.");
        }
        try {
            return vehicleDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículo por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar veículo. Tente novamente mais tarde.", e);
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
            throw new BusinessException("Erro de banco de dados ao buscar veículo por placa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() throws BusinessException, SQLException {
        try {
            return vehicleDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os veículos: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar veículos. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) throws BusinessException, SQLException {
        if (vehicle == null || !validator.isValidId(vehicle.getId())) {
            throw new BusinessException("Dados do veículo ou ID inválido para atualização.");
        }
        if (!validator.isValidId(vehicle.getCompanyId())) {
            throw new BusinessException("ID da empresa inválido para o veículo.");
        }
        if (!validator.isValidPlate(vehicle.getPlate())) {
            throw new BusinessException("Placa do veículo inválida.");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new BusinessException("Modelo do veículo é obrigatório.");
        }
        // REMOVIDO: Validação de Manufacturer
        // if (vehicle.getManufacturer() == null || vehicle.getManufacturer().trim().isEmpty()) {
        //     throw new BusinessException("Fabricante do veículo é obrigatório.");
        // }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDateTime.now().getYear() + 1) {
            throw new BusinessException("Ano do veículo inválido. Deve ser entre 1900 e o ano atual + 1.");
        }

        try {
            Optional<Vehicle> existingVehicle = vehicleDAO.findById(vehicle.getId());
            if (!existingVehicle.isPresent()) {
                throw new BusinessException("Veículo com ID " + vehicle.getId() + " não encontrado para atualização.");
            }

            // Validar se a empresa existe
            if (!companyDAO.findById(vehicle.getCompanyId()).isPresent()) {
                throw new BusinessException("Empresa com ID " + vehicle.getCompanyId() + " não encontrada.");
            }

            // Verificar unicidade da placa, exceto se for a placa do próprio veículo que está sendo atualizado
            Optional<Vehicle> vehicleByPlate = vehicleDAO.findByPlate(vehicle.getPlate());
            if (vehicleByPlate.isPresent() && vehicleByPlate.get().getId() != vehicle.getId()) {
                throw new BusinessException("Já existe outro veículo cadastrado com esta placa.");
            }

            vehicle.setCreatedAt(existingVehicle.get().getCreatedAt()); // Mantém a data de criação original
            vehicle.setUpdatedAt(LocalDateTime.now());
            boolean updated = vehicleDAO.update(vehicle);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar veículo. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Veículo atualizado com sucesso: ID {0}", vehicle.getId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteVehicle(int id) throws BusinessException, SQLException {
        if (!validator.isValidId(id)) {
            throw new BusinessException("ID do veículo inválido para exclusão.");
        }
        try {
            Optional<Vehicle> existingVehicle = vehicleDAO.findById(id);
            if (!existingVehicle.isPresent()) {
                throw new BusinessException("Veículo com ID " + id + " não encontrado para exclusão.");
            }
            boolean deleted = vehicleDAO.delete(id);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar veículo. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Veículo deletado com sucesso. ID: {0}", id);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getVehiclesByCompanyId(int companyId) throws BusinessException, SQLException {
        if (!validator.isValidId(companyId)) {
            throw new BusinessException("ID da empresa inválido para busca de veículos.");
        }
        try {
            return vehicleDAO.findByCompanyId(companyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículos por ID da empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar veículos por empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getVehiclesByModel(String model) throws BusinessException, SQLException {
        if (model == null || model.trim().isEmpty()) {
            throw new BusinessException("Modelo do veículo não pode ser vazio para busca.");
        }
        try {
            return vehicleDAO.findByModel(model);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículos por modelo: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar veículos por modelo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getVehiclesByYear(int year) throws BusinessException, SQLException {
        if (year <= 1900 || year > LocalDateTime.now().getYear() + 1) {
            throw new BusinessException("Ano do veículo inválido para busca. Deve ser entre 1900 e o ano atual + 1.");
        }
        try {
            return vehicleDAO.findByYear(year);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículos por ano: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar veículos por ano. Tente novamente mais tarde.", e);
        }
    }
}
