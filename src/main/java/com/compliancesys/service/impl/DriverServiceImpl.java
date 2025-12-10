package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Importar a classe Validator

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.util.Validator;

public class DriverServiceImpl implements DriverService {

    private final DriverDAO driverDAO;
    private final Validator validator; // Adicionar o campo Validator

    // Construtor que aceita DriverDAO e Validator
    public DriverServiceImpl(DriverDAO driverDAO, Validator validator) {
        this.driverDAO = driverDAO;
        this.validator = validator;
    }

    @Override
    public Driver createDriver(Driver driver) throws BusinessException, SQLException {
        // Validações de negócio
        if (!validator.isValidCpf(driver.getCpf())) {
            throw new BusinessException("CPF inválido.");
        }
        if (driverDAO.findByCpf(driver.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado.");
        }
        if (driverDAO.findByLicenseNumber(driver.getLicenseNumber()).isPresent()) {
            throw new BusinessException("Número de CNH já cadastrado.");
        }
        if (driver.getLicenseExpiration() != null && driver.getLicenseExpiration().isBefore(LocalDate.now())) {
            throw new BusinessException("CNH vencida.");
        }
        if (driver.getBirthDate() != null && driver.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessException("Motorista deve ter pelo menos 18 anos.");
        }
        if (driver.getEmail() != null && !driver.getEmail().isEmpty() && driverDAO.findByEmail(driver.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado.");
        }
        if (driver.getPhone() != null && !driver.getPhone().isEmpty() && driverDAO.findByPhone(driver.getPhone()).isPresent()) {
            throw new BusinessException("Telefone já cadastrado.");
        }

        // Define datas de criação e atualização
        LocalDateTime now = LocalDateTime.now();
        driver.setCreatedAt(now);
        driver.setUpdatedAt(now);

        int id = driverDAO.create(driver);
        driver.setId(id);
        return driver;
    }

    @Override
    public Optional<Driver> getDriverById(int id) throws BusinessException, SQLException {
        if (id <= 0) {
            throw new BusinessException("ID do motorista inválido.");
        }
        return driverDAO.findById(id);
    }

    @Override
    public List<Driver> getAllDrivers() throws SQLException {
        return driverDAO.findAll();
    }

    @Override
    public Driver updateDriver(Driver driver) throws BusinessException, SQLException {
        if (driver.getId() <= 0) {
            throw new BusinessException("ID do motorista inválido para atualização.");
        }

        Optional<Driver> existingDriverOpt = driverDAO.findById(driver.getId());
        if (!existingDriverOpt.isPresent()) { // CORRIGIDO: isEmpty() -> !isPresent()
            throw new BusinessException("Motorista não encontrado para atualização.");
        }

        Driver existingDriver = existingDriverOpt.get();

        // Validações de CPF e CNH (se alterados)
        if (!driver.getCpf().equals(existingDriver.getCpf())) {
            if (!validator.isValidCpf(driver.getCpf())) {
                throw new BusinessException("CPF inválido.");
            }
            if (driverDAO.findByCpf(driver.getCpf()).isPresent()) {
                throw new BusinessException("CPF já cadastrado por outro motorista.");
            }
        }
        if (!driver.getLicenseNumber().equals(existingDriver.getLicenseNumber())) {
            if (driverDAO.findByLicenseNumber(driver.getLicenseNumber()).isPresent()) {
                throw new BusinessException("Número de CNH já cadastrado por outro motorista.");
            }
        }
        if (driver.getLicenseExpiration() != null && driver.getLicenseExpiration().isBefore(LocalDate.now())) {
            throw new BusinessException("CNH vencida.");
        }
        if (driver.getBirthDate() != null && driver.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessException("Motorista deve ter pelo menos 18 anos.");
        }
        if (driver.getEmail() != null && !driver.getEmail().isEmpty() && !driver.getEmail().equals(existingDriver.getEmail())) {
            if (driverDAO.findByEmail(driver.getEmail()).isPresent()) {
                throw new BusinessException("Email já cadastrado por outro motorista.");
            }
        }
        if (driver.getPhone() != null && !driver.getPhone().isEmpty() && !driver.getPhone().equals(existingDriver.getPhone())) {
            if (driverDAO.findByPhone(driver.getPhone()).isPresent()) {
                throw new BusinessException("Telefone já cadastrado por outro motorista.");
            }
        }

        // Atualiza os campos do motorista existente com os novos valores
        existingDriver.setCompanyId(driver.getCompanyId());
        existingDriver.setName(driver.getName());
        existingDriver.setCpf(driver.getCpf());
        existingDriver.setLicenseNumber(driver.getLicenseNumber());
        existingDriver.setLicenseCategory(driver.getLicenseCategory());
        existingDriver.setLicenseExpiration(driver.getLicenseExpiration());
        existingDriver.setBirthDate(driver.getBirthDate());
        existingDriver.setPhone(driver.getPhone());
        existingDriver.setEmail(driver.getEmail());
        existingDriver.setUpdatedAt(LocalDateTime.now());

        boolean updated = driverDAO.update(existingDriver);
        if (!updated) {
            throw new BusinessException("Falha ao atualizar motorista.");
        }
        return existingDriver;
    }

    @Override
    public boolean deleteDriver(int id) throws SQLException {
        return driverDAO.delete(id);
    }

    @Override
    public Optional<Driver> getDriverByCpf(String cpf) throws BusinessException, SQLException {
        if (!validator.isValidCpf(cpf)) {
            throw new BusinessException("CPF inválido.");
        }
        return driverDAO.findByCpf(cpf);
    }

    @Override
    public Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws SQLException {
        if (licenseNumber == null || licenseNumber.isEmpty()) {
            throw new BusinessException("Número de CNH não pode ser vazio.");
        }
        return driverDAO.findByLicenseNumber(licenseNumber);
    }

    @Override
    public List<Driver> getDriversByCompanyId(int companyId) throws SQLException {
        if (companyId <= 0) {
            throw new BusinessException("ID da empresa inválido.");
        }
        return driverDAO.findByCompanyId(companyId);
    }

    @Override
    public List<Driver> getDriversByName(String name) throws SQLException {
        if (name == null || name.isEmpty()) {
            throw new BusinessException("Nome não pode ser vazio.");
        }
        return driverDAO.findByName(name);
    }

    @Override
    public List<Driver> getDriversByLicenseCategory(String licenseCategory) throws SQLException {
        if (licenseCategory == null || licenseCategory.isEmpty()) {
            throw new BusinessException("Categoria da CNH não pode ser vazia.");
        }
        return driverDAO.findByLicenseCategory(licenseCategory);
    }

    @Override
    public List<Driver> getDriversByLicenseExpirationBefore(LocalDate date) throws SQLException {
        if (date == null) {
            throw new BusinessException("Data de expiração não pode ser nula.");
        }
        return driverDAO.findByLicenseExpirationBefore(date);
    }

    @Override
    public List<Driver> getDriversByBirthDateBetween(LocalDate startDate, LocalDate endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Datas de nascimento não podem ser nulas.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Data de início não pode ser posterior à data de fim.");
        }
        return driverDAO.findByBirthDateBetween(startDate, endDate);
    }

    @Override
    public Optional<Driver> getDriverByPhone(String phone) throws BusinessException, SQLException {
        if (phone == null || phone.isEmpty()) {
            throw new BusinessException("Telefone não pode ser vazio.");
        }
        return driverDAO.findByPhone(phone);
    }

    @Override
    public Optional<Driver> getDriverByEmail(String email) throws BusinessException, SQLException {
        if (email == null || email.isEmpty()) {
            throw new BusinessException("Email não pode ser vazio.");
        }
        return driverDAO.findByEmail(email);
    }
}
