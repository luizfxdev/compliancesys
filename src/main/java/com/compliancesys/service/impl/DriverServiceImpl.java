package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.util.Validator;

public class DriverServiceImpl implements DriverService {

    private static final Logger LOGGER = Logger.getLogger(DriverServiceImpl.class.getName());
    private final DriverDAO driverDAO;
    private final Validator validator;

    public DriverServiceImpl(DriverDAO driverDAO, Validator validator) {
        this.driverDAO = driverDAO;
        this.validator = validator;
    }

    @Override
    public Driver registerDriver(Driver driver) throws SQLException, BusinessException {
        if (driver == null) {
            throw new BusinessException("Dados do motorista não podem ser nulos.");
        }
        validator.validate(driver);

        if (driverDAO.findByCpf(driver.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado para outro motorista.");
        }
        if (driverDAO.findByLicenseNumber(driver.getLicenseNumber()).isPresent()) {
            throw new BusinessException("Número de licença já cadastrado para outro motorista.");
        }
        if (driver.getEmail() != null && !driver.getEmail().isEmpty() && driverDAO.findByEmail(driver.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado para outro motorista.");
        }

        int id = driverDAO.create(driver);
        driver.setId(id);
        LOGGER.log(Level.INFO, "Motorista registrado com ID: {0}", id);
        return driver;
    }

    @Override
    public Driver updateDriver(Driver driver) throws SQLException, BusinessException {
        if (driver == null || driver.getId() <= 0) {
            throw new BusinessException("Dados do motorista ou ID inválidos para atualização.");
        }
        validator.validate(driver);

        Optional<Driver> existingDriverOptional = driverDAO.findById(driver.getId());
        if (!existingDriverOptional.isPresent()) {
            throw new BusinessException("Motorista não encontrado para atualização.");
        }

        Optional<Driver> driverByCpf = driverDAO.findByCpf(driver.getCpf());
        if (driverByCpf.isPresent() && driverByCpf.get().getId() != driver.getId()) {
            throw new BusinessException("CPF já cadastrado para outro motorista.");
        }

        Optional<Driver> driverByLicense = driverDAO.findByLicenseNumber(driver.getLicenseNumber());
        if (driverByLicense.isPresent() && driverByLicense.get().getId() != driver.getId()) {
            throw new BusinessException("Número de licença já cadastrado para outro motorista.");
        }

        if (driver.getEmail() != null && !driver.getEmail().isEmpty()) {
            Optional<Driver> driverByEmail = driverDAO.findByEmail(driver.getEmail());
            if (driverByEmail.isPresent() && driverByEmail.get().getId() != driver.getId()) {
                throw new BusinessException("Email já cadastrado para outro motorista.");
            }
        }

        if (!driverDAO.update(driver)) {
            throw new BusinessException("Falha ao atualizar motorista.");
        }
        LOGGER.log(Level.INFO, "Motorista atualizado com ID: {0}", driver.getId());
        return driver;
    }

    @Override
    public boolean deleteDriver(int id) throws SQLException, BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID do motorista inválido para exclusão.");
        }
        if (!driverDAO.findById(id).isPresent()) {
            throw new BusinessException("Motorista não encontrado para exclusão.");
        }
        boolean deleted = driverDAO.delete(id);
        if (deleted) {
            LOGGER.log(Level.INFO, "Motorista deletado com ID: {0}", id);
        } else {
            LOGGER.log(Level.WARNING, "Falha ao deletar motorista com ID: {0}", id);
        }
        return deleted;
    }

    @Override
    public Optional<Driver> getDriverById(int id) throws SQLException {
        if (id <= 0) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar motorista com ID inválido: {0}", id);
            return Optional.empty();
        }
        return driverDAO.findById(id);
    }

    @Override
    public List<Driver> getAllDrivers() throws SQLException {
        return driverDAO.findAll();
    }

    @Override
    public Optional<Driver> getDriverByCpf(String cpf) throws SQLException {
        if (cpf == null || cpf.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar motorista com CPF nulo ou vazio.");
            return Optional.empty();
        }
        return driverDAO.findByCpf(cpf);
    }

    @Override
    public Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws SQLException {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar motorista com número de licença nulo ou vazio.");
            return Optional.empty();
        }
        return driverDAO.findByLicenseNumber(licenseNumber);
    }

    @Override
    public List<Driver> getDriversByCompanyId(int companyId) throws SQLException {
        if (companyId <= 0) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar motoristas com ID de empresa inválido: {0}", companyId);
            return new ArrayList<>();
        }
        return driverDAO.findByCompanyId(companyId);
    }

    @Override
    public Optional<Driver> getDriverByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Tentativa de buscar motorista com email nulo ou vazio.");
            return Optional.empty();
        }
        return driverDAO.findByEmail(email);
    }
}