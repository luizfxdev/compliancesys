package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level; // Importa o Validator
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.util.Validator;

public class DriverServiceImpl implements DriverService {

    private static final Logger LOGGER = Logger.getLogger(DriverServiceImpl.class.getName());
    private final DriverDAO driverDAO;
    private final Validator validator; // Injeta o Validator

    public DriverServiceImpl(DriverDAO driverDAO, Validator validator) {
        this.driverDAO = driverDAO;
        this.validator = validator;
    }

    @Override
    public Driver registerDriver(Driver driver) throws BusinessException, SQLException {
        if (driver == null) {
            throw new BusinessException("Motorista não pode ser nulo.");
        }
        if (driver.getCompanyId() <= 0) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (!validator.isValidName(driver.getName())) {
            throw new BusinessException("Nome do motorista inválido.");
        }
        if (!validator.isValidCpf(driver.getCpf())) {
            throw new BusinessException("CPF do motorista inválido.");
        }
        if (!validator.isValidLicenseNumber(driver.getLicenseNumber())) {
            throw new BusinessException("Número da licença do motorista inválido.");
        }
        if (driver.getBirthDate() == null) { // CORRIGIDO: getBirthDate()
            throw new BusinessException("Data de nascimento do motorista é obrigatória.");
        }
        if (!validator.isPastOrPresentDate(driver.getBirthDate())) {
            throw new BusinessException("Data de nascimento não pode ser futura.");
        }

        try {
            // Verifica se já existe um motorista com o mesmo CPF
            if (driverDAO.findByCpf(driver.getCpf()).isPresent()) {
                throw new BusinessException("Já existe um motorista cadastrado com este CPF.");
            }

            driver.setCreatedAt(LocalDateTime.now());
            driver.setUpdatedAt(LocalDateTime.now());
            int id = driverDAO.create(driver);
            driver.setId(id);
            LOGGER.log(Level.INFO, "Motorista registrado com sucesso: ID {0}", id);
            return driver;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao registrar motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverById(int driverId) throws BusinessException, SQLException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        try {
            return driverDAO.findById(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverByCpf(String cpf) throws BusinessException, SQLException {
        if (!validator.isValidCpf(cpf)) {
            throw new BusinessException("CPF inválido para busca.");
        }
        try {
            return driverDAO.findByCpf(cpf);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por CPF: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Driver> getAllDrivers() throws BusinessException, SQLException {
        try {
            return driverDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os motoristas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar motoristas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Driver updateDriver(Driver driver) throws BusinessException, SQLException {
        if (driver == null || driver.getId() <= 0) {
            throw new BusinessException("Motorista ou ID inválido para atualização.");
        }
        if (driver.getCompanyId() <= 0) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (!validator.isValidName(driver.getName())) {
            throw new BusinessException("Nome do motorista inválido.");
        }
        if (!validator.isValidCpf(driver.getCpf())) {
            throw new BusinessException("CPF do motorista inválido.");
        }
        if (!validator.isValidLicenseNumber(driver.getLicenseNumber())) {
            throw new BusinessException("Número da licença do motorista inválido.");
        }
        if (driver.getBirthDate() == null) { // CORRIGIDO: getBirthDate()
            throw new BusinessException("Data de nascimento do motorista é obrigatória.");
        }
        if (!validator.isPastOrPresentDate(driver.getBirthDate())) {
            throw new BusinessException("Data de nascimento não pode ser futura.");
        }

        try {
            Optional<Driver> existingDriver = driverDAO.findById(driver.getId());
            if (existingDriver.isEmpty()) {
                throw new BusinessException("Motorista com ID " + driver.getId() + " não encontrado para atualização.");
            }

            // Verifica se o CPF foi alterado e se o novo CPF já existe para outro motorista
            if (!existingDriver.get().getCpf().equals(driver.getCpf())) {
                if (driverDAO.findByCpf(driver.getCpf()).isPresent()) {
                    throw new BusinessException("Já existe outro motorista cadastrado com o CPF informado.");
                }
            }

            driver.setUpdatedAt(LocalDateTime.now());
            driver.setCreatedAt(existingDriver.get().getCreatedAt()); // Mantém a data de criação original

            boolean updated = driverDAO.update(driver);
            if (updated) {
                LOGGER.log(Level.INFO, "Motorista atualizado com sucesso: ID {0}", driver.getId());
                return driver;
            } else {
                throw new BusinessException("Falha ao atualizar motorista. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteDriver(int driverId) throws BusinessException, SQLException {
        if (driverId <= 0) {
            throw new BusinessException("ID do motorista inválido para exclusão.");
        }
        try {
            Optional<Driver> existingDriver = driverDAO.findById(driverId);
            if (existingDriver.isEmpty()) {
                throw new BusinessException("Motorista com ID " + driverId + " não encontrado para exclusão.");
            }
            boolean deleted = driverDAO.delete(driverId);
            if (deleted) {
                LOGGER.log(Level.INFO, "Motorista com ID {0} deletado com sucesso.", driverId);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar motorista com ID {0}.", driverId);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar motorista. Tente novamente mais tarde.", e);
        }
    }
}
