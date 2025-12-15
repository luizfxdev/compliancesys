// src/main/java/com/compliancesys/service/impl/CompanyServiceImpl.java
package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.util.Validator;

public class CompanyServiceImpl implements CompanyService {
    private static final Logger LOGGER = Logger.getLogger(CompanyServiceImpl.class.getName());
    private final CompanyDAO companyDAO;
    private final Validator validator;

    public CompanyServiceImpl(CompanyDAO companyDAO, Validator validator) {
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    @Override
    // Renomeado de registerCompany para createCompany para alinhar com a interface e o Servlet
    public Company createCompany(Company company) throws BusinessException, SQLException {
        if (company == null) {
            throw new BusinessException("Dados da empresa não podem ser nulos.");
        }
        if (company.getLegalName() == null || company.getLegalName().trim().isEmpty()) {
            throw new BusinessException("Razão social da empresa é obrigatória.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        // Removidas validações para campos 'address', 'phone', 'email'
        // pois não existem no modelo Company atual que você forneceu.
        // Se esses campos forem adicionados ao modelo, as validações podem ser reativadas.

        try {
            if (companyDAO.findByCnpj(company.getCnpj()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ.");
            }
            if (companyDAO.findByLegalName(company.getLegalName()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com esta razão social.");
            }
            if (company.getTradingName() != null && !company.getTradingName().trim().isEmpty() &&
                companyDAO.findByTradingName(company.getTradingName()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este nome fantasia.");
            }

            company.setCreatedAt(LocalDateTime.now());
            company.setUpdatedAt(LocalDateTime.now());
            int id = companyDAO.create(company);
            if (id <= 0) {
                throw new BusinessException("Falha ao criar empresa. Nenhuma linha afetada.");
            }
            company.setId(id);
            LOGGER.log(Level.INFO, "Empresa registrada com sucesso. ID: {0}", id);
            return company;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao registrar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyById(int companyId) throws BusinessException, SQLException {
        if (!validator.isValidId(companyId)) {
            throw new BusinessException("ID da empresa inválido.");
        }
        try {
            return companyDAO.findById(companyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException, SQLException {
        if (!validator.isValidCnpj(cnpj)) {
            throw new BusinessException("CNPJ inválido para busca.");
        }
        try {
            return companyDAO.findByCnpj(cnpj);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por CNPJ: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Company> getAllCompanies() throws BusinessException, SQLException {
        try {
            return companyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as empresas: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Company updateCompany(Company company) throws BusinessException, SQLException {
        if (company == null || !validator.isValidId(company.getId())) {
            throw new BusinessException("Dados da empresa ou ID inválido para atualização.");
        }
        if (company.getLegalName() == null || company.getLegalName().trim().isEmpty()) {
            throw new BusinessException("Razão social da empresa é obrigatória.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        // Removidas validações para campos 'address', 'phone', 'email'
        // pois não existem no modelo Company atual que você forneceu.

        try {
            Optional<Company> existingCompany = companyDAO.findById(company.getId());
            if (!existingCompany.isPresent()) {
                throw new BusinessException("Empresa com ID " + company.getId() + " não encontrada para atualização.");
            }

            // Verificar duplicidade de CNPJ, LegalName e TradingName (excluindo a própria empresa)
            Optional<Company> companyByCnpj = companyDAO.findByCnpj(company.getCnpj());
            if (companyByCnpj.isPresent() && companyByCnpj.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com este CNPJ.");
            }
            Optional<Company> companyByLegalName = companyDAO.findByLegalName(company.getLegalName());
            if (companyByLegalName.isPresent() && companyByLegalName.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com esta razão social.");
            }
            if (company.getTradingName() != null && !company.getTradingName().trim().isEmpty()) {
                Optional<Company> companyByTradingName = companyDAO.findByTradingName(company.getTradingName());
                if (companyByTradingName.isPresent() && companyByTradingName.get().getId() != company.getId()) {
                    throw new BusinessException("Já existe outra empresa cadastrada com este nome fantasia.");
                }
            }

            company.setUpdatedAt(LocalDateTime.now());
            boolean updated = companyDAO.update(company);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar empresa. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Empresa atualizada com sucesso. ID: {0}", company.getId());
            return company;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao atualizar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteCompany(int companyId) throws BusinessException, SQLException {
        if (!validator.isValidId(companyId)) {
            throw new BusinessException("ID da empresa inválido para exclusão.");
        }
        try {
            Optional<Company> existingCompany = companyDAO.findById(companyId);
            if (!existingCompany.isPresent()) {
                throw new BusinessException("Empresa com ID " + companyId + " não encontrada para exclusão.");
            }
            // TODO: Adicionar lógica para verificar se existem motoristas ou veículos associados antes de deletar
            boolean deleted = companyDAO.delete(companyId);
            if (!deleted) {
                throw new BusinessException("Falha ao deletar empresa. Nenhuma linha afetada.");
            }
            LOGGER.log(Level.INFO, "Empresa deletada com sucesso. ID: {0}", companyId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByLegalName(String legalName) throws BusinessException, SQLException {
        if (legalName == null || legalName.trim().isEmpty()) {
            throw new BusinessException("Razão social não pode ser nula ou vazia para busca.");
        }
        try {
            return companyDAO.findByLegalName(legalName);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por razão social: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByTradingName(String tradingName) throws BusinessException, SQLException {
        if (tradingName == null || tradingName.trim().isEmpty()) {
            throw new BusinessException("Nome fantasia não pode ser nulo ou vazio para busca.");
        }
        try {
            return companyDAO.findByTradingName(tradingName);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por nome fantasia: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByEmail(String email) throws BusinessException, SQLException {
        // Se o modelo Company não tem email, este método não deveria existir ou deveria ser ajustado.
        // Assumindo que o DAO tem findByEmail, mas o modelo não o expõe.
        // Se o modelo Company não tem email, esta validação é irrelevante.
        // Para fins de compilação, vamos manter, mas é um ponto a revisar no design.
        if (!validator.isValidEmail(email)) {
            throw new BusinessException("Email inválido para busca.");
        }
        try {
            return companyDAO.findByEmail(email);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por email: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByPhone(String phone) throws BusinessException, SQLException {
        // Se o modelo Company não tem phone, este método não deveria existir ou deveria ser ajustado.
        // Assumindo que o DAO tem findByPhone, mas o modelo não o expõe.
        // Se o modelo Company não tem phone, esta validação é irrelevante.
        // Para fins de compilação, vamos manter, mas é um ponto a revisar no design.
        if (!validator.isValidPhone(phone)) {
            throw new BusinessException("Telefone inválido para busca.");
        }
        try {
            return companyDAO.findByPhone(phone);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por telefone: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }
}
