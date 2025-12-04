package com.compliancesys.service.impl;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.impl.DriverDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface DriverService.
 * Contém a lógica de negócio para a entidade Driver, interagindo com a camada DAO.
 */
public class DriverServiceImpl implements DriverService {

    private static final Logger LOGGER = Logger.getLogger(DriverServiceImpl.class.getName());
    private final DriverDAO driverDAO;

    /**
     * Construtor padrão que inicializa o DriverDAOImpl.
     * Pode ser estendido para injeção de dependência em um ambiente de produção.
     */
    public DriverServiceImpl() {
        this.driverDAO = new DriverDAOImpl();
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param driverDAO A implementação de DriverDAO a ser utilizada.
     */
    public DriverServiceImpl(DriverDAO driverDAO) {
        this.driverDAO = driverDAO;
    }

    @Override
    public Driver createDriver(Driver driver) throws BusinessException {
        // Validações de negócio antes de criar o motorista
        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            throw new BusinessException("O nome do motorista não pode ser vazio.");
        }
        if (driver.getCpf() == null || driver.getCpf().trim().isEmpty()) {
            throw new BusinessException("O CPF do motorista não pode ser vazio.");
        }
        // Exemplo de validação de formato de CPF (simplificado)
        if (!driver.getCpf().matches("\\d{11}")) { // Apenas dígitos, 11 caracteres
            throw new BusinessException("O CPF deve conter 11 dígitos numéricos.");
        }
        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            throw new BusinessException("O número da CNH do motorista não pode ser vazio.");
        }
        if (driver.getLicenseCategory() == null || driver.getLicenseCategory().trim().isEmpty()) {
            throw new BusinessException("A categoria da CNH do motorista não pode ser vazia.");
        }
        if (driver.getLicenseExpirationDate() == null) {
            throw new BusinessException("A data de validade da CNH é obrigatória.");
        }
        if (driver.getLicenseExpirationDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new BusinessException("A CNH do motorista está vencida.");
        }

        try {
            // Verifica se já existe um motorista com o mesmo CPF
            Optional<Driver> existingDriver = driverDAO.findByCpf(driver.getCpf());
            if (existingDriver.isPresent()) {
                throw new BusinessException("Já existe um motorista cadastrado com este CPF: " + driver.getCpf());
            }

            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            driver.setCreatedAt(now);
            driver.setUpdatedAt(now);

            int id = driverDAO.create(driver);
            if (id > 0) {
                driver.setId(id);
                LOGGER.log(Level.INFO, "Motorista criado com sucesso: {0}", driver.getName());
                return driver;
            } else {
                throw new BusinessException("Falha ao criar o motorista. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar o motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo.");
        }
        try {
            return driverDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar o motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Driver> getDriverByCpf(String cpf) throws BusinessException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new BusinessException("O CPF do motorista não pode ser vazio para a busca.");
        }
        try {
            return driverDAO.findByCpf(cpf);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista por CPF: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar o motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Driver> getAllDrivers() throws BusinessException {
        try {
            return driverDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os motoristas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar os motoristas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Driver updateDriver(Driver driver) throws BusinessException {
        if (driver.getId() <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo para atualização.");
        }
        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            throw new BusinessException("O nome do motorista não pode ser vazio.");
        }
        if (driver.getCpf() == null || driver.getCpf().trim().isEmpty()) {
            throw new BusinessException("O CPF do motorista não pode ser vazio.");
        }
        if (!driver.getCpf().matches("\\d{11}")) {
            throw new BusinessException("O CPF deve conter 11 dígitos numéricos.");
        }
        if (driver.getLicenseNumber() == null || driver.getLicenseNumber().trim().isEmpty()) {
            throw new BusinessException("O número da CNH do motorista não pode ser vazio.");
        }
        if (driver.getLicenseCategory() == null || driver.getLicenseCategory().trim().isEmpty()) {
            throw new BusinessException("A categoria da CNH do motorista não pode ser vazia.");
        }
        if (driver.getLicenseExpirationDate() == null) {
            throw new BusinessException("A data de validade da CNH é obrigatória.");
        }
        if (driver.getLicenseExpirationDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new BusinessException("A CNH do motorista está vencida.");
        }

        try {
            // Verifica se o motorista a ser atualizado existe
            Optional<Driver> existingDriverById = driverDAO.findById(driver.getId());
            if (existingDriverById.isEmpty()) {
                throw new BusinessException("Motorista com ID " + driver.getId() + " não encontrado para atualização.");
            }

            // Verifica se o novo CPF já pertence a outro motorista (que não seja o próprio)
            Optional<Driver> existingDriverByCpf = driverDAO.findByCpf(driver.getCpf());
            if (existingDriverByCpf.isPresent() && existingDriverByCpf.get().getId() != driver.getId()) {
                throw new BusinessException("Já existe outro motorista cadastrado com este CPF: " + driver.getCpf());
            }

            // Define a data de atualização
            driver.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            driver.setCreatedAt(existingDriverById.get().getCreatedAt());

            boolean updated = driverDAO.update(driver);
            if (updated) {
                LOGGER.log(Level.INFO, "Motorista atualizado com sucesso: {0}", driver.getName());
                return driver;
            } else {
                throw new BusinessException("Falha ao atualizar o motorista. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar o motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteDriver(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se o motorista existe antes de tentar deletar
            Optional<Driver> existingDriver = driverDAO.findById(id);
            if (existingDriver.isEmpty()) {
                throw new BusinessException("Motorista com ID " + id + " não encontrado para exclusão.");
            }
            // Adicionar lógica para verificar dependências (ex: se há jornadas ou registros de ponto associados)
            // Se houver, lançar BusinessException ou tratar a exclusão em cascata (se o DB permitir e for desejado)

            boolean deleted = driverDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Motorista com ID {0} deletado com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar motorista com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar o motorista. Tente novamente mais tarde.", e);
        }
    }
}
