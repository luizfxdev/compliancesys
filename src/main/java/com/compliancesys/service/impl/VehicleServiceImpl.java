package com.compliancesys.service.impl;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.dao.impl.VehicleDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface VehicleService.
 * Contém a lógica de negócio para a entidade Vehicle, interagindo com a camada DAO.
 */
public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOGGER = Logger.getLogger(VehicleServiceImpl.class.getName());
    private final VehicleDAO vehicleDAO;
    private final CompanyDAO companyDAO; // Necessário para validar a existência da empresa

    /**
     * Construtor padrão que inicializa os DAOs.
     */
    public VehicleServiceImpl() {
        this.vehicleDAO = new VehicleDAOImpl();
        this.companyDAO = new CompanyDAOImpl();
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param vehicleDAO A implementação de VehicleDAO a ser utilizada.
     * @param companyDAO A implementação de CompanyDAO a ser utilizada.
     */
    public VehicleServiceImpl(VehicleDAO vehicleDAO, CompanyDAO companyDAO) {
        this.vehicleDAO = vehicleDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) throws BusinessException {
        // Validações de negócio antes de criar o veículo
        if (vehicle.getCompanyId() <= 0) {
            throw new BusinessException("O ID da empresa é obrigatório e deve ser um valor positivo.");
        }
        if (vehicle.getPlate() == null || vehicle.getPlate().trim().isEmpty()) {
            throw new BusinessException("A placa do veículo não pode ser vazia.");
        }
        // Exemplo de validação de formato de placa (Mercosul ou antiga)
        if (!vehicle.getPlate().matches("[A-Z]{3}\\d[A-Z0-9]\\d{2}")) { // Padrão Mercosul ou antigo (simplificado)
            throw new BusinessException("A placa do veículo não está em um formato válido (ex: ABC1D23 ou ABC1234).");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new BusinessException("O modelo do veículo não pode ser vazio.");
        }
        if (vehicle.getManufacturer() == null || vehicle.getManufacturer().trim().isEmpty()) {
            throw new BusinessException("O fabricante do veículo não pode ser vazio.");
        }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDateTime.now().getYear() + 1) { // Ano razoável
            throw new BusinessException("O ano do veículo é inválido.");
        }

        try {
            // Verifica se a empresa associada existe
            Optional<Company> existingCompany = companyDAO.findById(vehicle.getCompanyId());
            if (existingCompany.isEmpty()) {
                throw new BusinessException("Empresa com ID " + vehicle.getCompanyId() + " não encontrada. Não é possível criar o veículo.");
            }

            // Verifica se já existe um veículo com a mesma placa
            Optional<Vehicle> existingVehicle = vehicleDAO.findByPlate(vehicle.getPlate());
            if (existingVehicle.isPresent()) {
                throw new BusinessException("Já existe um veículo cadastrado com esta placa: " + vehicle.getPlate());
            }

            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            vehicle.setCreatedAt(now);
            vehicle.setUpdatedAt(now);

            int id = vehicleDAO.create(vehicle);
            if (id > 0) {
                vehicle.setId(id);
                LOGGER.log(Level.INFO, "Veículo criado com sucesso: {0} - {1}", new Object[]{vehicle.getPlate(), vehicle.getModel()});
                return vehicle;
            } else {
                throw new BusinessException("Falha ao criar o veículo. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar o veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Vehicle> getVehicleById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do veículo deve ser um valor positivo.");
        }
        try {
            return vehicleDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículo por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar o veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Vehicle> getVehicleByPlate(String plate) throws BusinessException {
        if (plate == null || plate.trim().isEmpty()) {
            throw new BusinessException("A placa do veículo não pode ser vazia para a busca.");
        }
        try {
            return vehicleDAO.findByPlate(plate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículo por placa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar o veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() throws BusinessException {
        try {
            return vehicleDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os veículos: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar os veículos. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Vehicle> getVehiclesByCompanyId(int companyId) throws BusinessException {
        if (companyId <= 0) {
            throw new BusinessException("O ID da empresa deve ser um valor positivo.");
        }
        try {
            return vehicleDAO.findByCompanyId(companyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículos por ID de empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar veículos por empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Vehicle updateVehicle(Vehicle vehicle) throws BusinessException {
        if (vehicle.getId() <= 0) {
            throw new BusinessException("O ID do veículo deve ser um valor positivo para atualização.");
        }
        if (vehicle.getCompanyId() <= 0) {
            throw new BusinessException("O ID da empresa é obrigatório e deve ser um valor positivo.");
        }
        if (vehicle.getPlate() == null || vehicle.getPlate().trim().isEmpty()) {
            throw new BusinessException("A placa do veículo não pode ser vazia.");
        }
        if (!vehicle.getPlate().matches("[A-Z]{3}\\d[A-Z0-9]\\d{2}")) {
            throw new BusinessException("A placa do veículo não está em um formato válido (ex: ABC1D23 ou ABC1234).");
        }
        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new BusinessException("O modelo do veículo não pode ser vazio.");
        }
        if (vehicle.getManufacturer() == null || vehicle.getManufacturer().trim().isEmpty()) {
            throw new BusinessException("O fabricante do veículo não pode ser vazio.");
        }
        if (vehicle.getYear() <= 1900 || vehicle.getYear() > LocalDateTime.now().getYear() + 1) {
            throw new BusinessException("O ano do veículo é inválido.");
        }

        try {
            // Verifica se o veículo a ser atualizado existe
            Optional<Vehicle> existingVehicleById = vehicleDAO.findById(vehicle.getId());
            if (existingVehicleById.isEmpty()) {
                throw new BusinessException("Veículo com ID " + vehicle.getId() + " não encontrado para atualização.");
            }

            // Verifica se a empresa associada existe
            Optional<Company> existingCompany = companyDAO.findById(vehicle.getCompanyId());
            if (existingCompany.isEmpty()) {
                throw new BusinessException("Empresa com ID " + vehicle.getCompanyId() + " não encontrada. Não é possível atualizar o veículo.");
            }

            // Verifica se a nova placa já pertence a outro veículo (que não seja o próprio)
            Optional<Vehicle> existingVehicleByPlate = vehicleDAO.findByPlate(vehicle.getPlate());
            if (existingVehicleByPlate.isPresent() && existingVehicleByPlate.get().getId() != vehicle.getId()) {
                throw new BusinessException("Já existe outro veículo cadastrado com esta placa: " + vehicle.getPlate());
            }

            // Define a data de atualização
            vehicle.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            vehicle.setCreatedAt(existingVehicleById.get().getCreatedAt());

            boolean updated = vehicleDAO.update(vehicle);
            if (updated) {
                LOGGER.log(Level.INFO, "Veículo atualizado com sucesso: {0} - {1}", new Object[]{vehicle.getPlate(), vehicle.getModel()});
                return vehicle;
            } else {
                throw new BusinessException("Falha ao atualizar o veículo. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar o veículo. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteVehicle(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do veículo deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se o veículo existe antes de tentar deletar
            Optional<Vehicle> existingVehicle = vehicleDAO.findById(id);
            if (existingVehicle.isEmpty()) {
                throw new BusinessException("Veículo com ID " + id + " não encontrado para exclusão.");
            }
            // Adicionar lógica para verificar dependências (ex: se há jornadas associadas a este veículo)
            // Se houver, lançar BusinessException ou tratar a exclusão em cascata (se o DB permitir e for desejado)

            boolean deleted = vehicleDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Veículo com ID {0} deletado com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar veículo com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar veículo: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar o veículo. Tente novamente mais tarde.", e);
        }
    }
}
