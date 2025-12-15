// src/main/java/com/compliancesys/service/impl/DriverServiceImpl.java
package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO; // Adicionado
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.util.Validator;
// import com.compliancesys.util.impl.ValidatorImpl; // Não é necessário importar a implementação

public class DriverServiceImpl implements DriverService {
    private static final Logger LOGGER = Logger.getLogger(DriverServiceImpl.class.getName());

    private final DriverDAO driverDAO;
    private final CompanyDAO companyDAO; // Adicionado
    private final Validator validator;

    // Construtor para injeção de dependência
    public DriverServiceImpl(DriverDAO driverDAO, CompanyDAO companyDAO, Validator validator) {
        this.driverDAO = driverDAO;
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    // REMOVIDO: Construtor padrão que instanciaria DAOs sem Connection
    // public DriverServiceImpl() {
    //     this.driverDAO = new DriverDAOImpl();
    //     this.validator = new ValidatorImpl();
    // }

    @Override
    public Driver registerDriver(Driver driver) throws BusinessException, SQLException {
        if (driver == null) {
            throw new BusinessException("Dados do motorista não podem ser nulos.");
        }
        if (!validator.isValidId(driver.getCompanyId())) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            throw new BusinessException("Nome do motorista é obrigatório.");
        }
        if (!validator.isValidCpf(driver.getCpf())) {
            throw new BusinessException("CPF inválido.");
        }
        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            throw new BusinessException("Número da CNH é obrigatório.");
        }
        if (driver.getLicenseCategory() == null || driver.getLicenseCategory().trim().isEmpty()) {
            throw new BusinessException("Categoria da CNH é obrigatória.");
        }
        if (driver.getLicenseExpiration() == null || driver.getLicenseExpiration().isBefore(LocalDate.now())) { // Usar getLicenseExpiration()
            throw new BusinessException("Data de validade da CNH inválida ou expirada.");
        }
        if (driver.getBirthDate() == null || driver.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessException("Data de nascimento inválida. Motorista deve ter pelo menos 18 anos.");
        }
        if (!validator.isValidPhone(driver.getPhone())) {
            throw new BusinessException("Telefone inválido.");
        }
        if (!validator.isValidEmail(driver.getEmail())) { // Usar isValidEmail()
            throw new BusinessException("Email inválido.");
        }

        try {
            // Validar se a empresa existe
            if (!companyDAO.findById(driver.getCompanyId()).isPresent()) {
                throw new BusinessException("Empresa com ID " + driver.getCompanyId() + " não encontrada.");
            }

            if (driverDAO.findByCpf(driver.getCpf()).isPresent()) {
                throw new BusinessException("Já existe um motorista cadastrado com este CPF.");
            }
            if (driverDAO.findByLicenseNumber(driver.getLicenseNumber()).isPresent()) {
                throw new BusinessException("Já existe um motorista cadastrado com este número de CNH.");
            }
            if (driverDAO.findByEmail(driver.getEmail()).isPresent()) {
                throw new BusinessException("Já existe um motorista cadastrado com este email.");
            }

            driver.setCreatedAt(LocalDateTime.now());
            driver.setUpdatedAt(LocalDateTime.now());
            int id = driverDAO.create(driver);
            if (id <= 0) {
                throw new BusinessException("Falha ao registrar motorista. Tente novamente.");
            }
            driver.setId(id);
            LOGGER.log(Level.INFO, "Motorista registrado com sucesso: {0}", driver.getName());
            return driver;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverById(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido.");
        }
        try {
            return driverDAO.findById(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar motorista. Tente novamente mais tarde.", e);
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
            throw new BusinessException("Erro de banco de dados ao buscar motorista por CPF. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverByLicenseNumber(String licenseNumber) throws BusinessException, SQLException {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new BusinessException("Número da CNH inválido para busca.");
        }
        try {
            return driverDAO.findByLicenseNumber(licenseNumber);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por CNH: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar motorista por CNH. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Driver> getDriversByCompanyId(int companyId) throws BusinessException, SQLException {
        if (!validator.isValidId(companyId)) {
            throw new BusinessException("ID da empresa inválido para busca de motoristas.");
        }
        try {
            return driverDAO.findByCompanyId(companyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motoristas por ID da empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar motoristas por empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverByEmail(String email) throws BusinessException, SQLException {
        if (!validator.isValidEmail(email)) {
            throw new BusinessException("Email inválido para busca.");
        }
        try {
            return driverDAO.findByEmail(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por email: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar motorista por email. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Driver> getAllDrivers() throws BusinessException, SQLException {
        try {
            return driverDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os motoristas: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar motoristas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Driver updateDriver(Driver driver) throws BusinessException, SQLException {
        if (driver == null || !validator.isValidId(driver.getId())) {
            throw new BusinessException("Dados do motorista ou ID inválido para atualização.");
        }
        if (!validator.isValidId(driver.getCompanyId())) {
            throw new BusinessException("ID da empresa inválido.");
        }
        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            throw new BusinessException("Nome do motorista é obrigatório.");
        }
        if (!validator.isValidCpf(driver.getCpf())) {
            throw new BusinessException("CPF inválido.");
        }
        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            throw new BusinessException("Número da CNH é obrigatório.");
        }
        if (driver.getLicenseCategory() == null || driver.getLicenseCategory().trim().isEmpty()) {
            throw new BusinessException("Categoria da CNH é obrigatória.");
        }
        if (driver.getLicenseExpiration() == null || driver.getLicenseExpiration().isBefore(LocalDate.now())) { // Usar getLicenseExpiration()
            throw new BusinessException("Data de validade da CNH inválida ou expirada.");
        }
        if (driver.getBirthDate() == null || driver.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new BusinessException("Data de nascimento inválida. Motorista deve ter pelo menos 18 anos.");
        }
        if (!validator.isValidPhone(driver.getPhone())) {
            throw new BusinessException("Telefone inválido.");
        }
        if (!validator.isValidEmail(driver.getEmail())) { // Usar isValidEmail()
            throw new BusinessException("Email inválido.");
        }

        try {
            Optional<Driver> existingDriver = driverDAO.findById(driver.getId());
            if (!existingDriver.isPresent()) {
                throw new BusinessException("Motorista com ID " + driver.getId() + " não encontrado para atualização.");
            }

            // Validar se a empresa existe
            if (!companyDAO.findById(driver.getCompanyId()).isPresent()) {
                throw new BusinessException("Empresa com ID " + driver.getCompanyId() + " não encontrada.");
            }

            // Verificar unicidade de CPF, CNH e Email, exceto se for do próprio motorista que está sendo atualizado
            Optional<Driver> driverByCpf = driverDAO.findByCpf(driver.getCpf());
            if (driverByCpf.isPresent() && driverByCpf.get().getId() != driver.getId()) {
                throw new BusinessException("Já existe outro motorista cadastrado com este CPF.");
            }
            Optional<Driver> driverByLicense = driverDAO.findByLicenseNumber(driver.getLicenseNumber());
            if (driverByLicense.isPresent() && driverByLicense.get().getId() != driver.getId()) {
                throw new BusinessException("Já existe outro motorista cadastrado com este número de CNH.");
            }
            Optional<Driver> driverByEmail = driverDAO.findByEmail(driver.getEmail());
            if (driverByEmail.isPresent() && driverByEmail.get().getId() != driver.getId()) {
                throw new BusinessException("Já existe outro motorista cadastrado com este email.");
            }

            driver.setCreatedAt(existingDriver.get().getCreatedAt()); // Mantém a data de criação original
            driver.setUpdatedAt(LocalDateTime.now());
            boolean updated = driverDAO.update(driver);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar motorista. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Motorista atualizado com sucesso: ID {0}", driver.getId());
            return driver;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteDriver(int driverId) throws BusinessException, SQLException {
        if (!validator.isValidId(driverId)) {
            throw new BusinessException("ID do motorista inválido para exclusão.");
        }
        try {
            Optional<Driver> existingDriver = driverDAO.findById(driverId);
            if (!existingDriver.isPresent()) {
                throw new BusinessException("Motorista com ID " + driverId + " não encontrado para exclusão.");
            }
            boolean deleted = driverDAO.delete(driverId);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar motorista. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Motorista deletado com sucesso. ID: {0}", driverId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar motorista. Tente novamente mais tarde.", e);
        }
    }
}
