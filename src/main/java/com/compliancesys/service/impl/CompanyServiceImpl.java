package com.compliancesys.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.ValidatorImpl;

public class CompanyServiceImpl implements CompanyService {

    private static final Logger LOGGER = Logger.getLogger(CompanyServiceImpl.class.getName());
    private final CompanyDAO companyDAO;
    private final Validator validator;

    public CompanyServiceImpl() {
        this.companyDAO = new CompanyDAOImpl();
        this.validator = new ValidatorImpl();
    }

    public CompanyServiceImpl(CompanyDAO companyDAO, Validator validator) {
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    @Override
    public Company registerCompany(Company company) throws BusinessException {
        if (company == null) {
            throw new BusinessException("Empresa não pode ser nula.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BusinessException("Nome da empresa é obrigatório.");
        }
        if (company.getAddress() == null || company.getAddress().trim().isEmpty()) {
            throw new BusinessException("Endereço da empresa é obrigatório.");
        }
        if (company.getPhone() == null || company.getPhone().trim().isEmpty()) {
            throw new BusinessException("Telefone da empresa é obrigatório.");
        }
        if (!validator.isValidEmail(company.getEmail())) {
            throw new BusinessException("Email da empresa inválido.");
        }

        try {
            if (companyDAO.findByCnpj(company.getCnpj()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ.");
            }

            company.setCreatedAt(LocalDateTime.now());
            company.setUpdatedAt(LocalDateTime.now());
            int id = companyDAO.create(company);
            company.setId(id);
            LOGGER.log(Level.INFO, "Empresa registrada com sucesso: ID {0}", id);
            return company;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao registrar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da empresa inválido.");
        }
        try {
            return companyDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Company> getAllCompanies() throws BusinessException {
        try {
            return companyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as empresas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar as empresas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Company updateCompany(Company company) throws BusinessException {
        if (company == null || company.getId() <= 0) {
            throw new BusinessException("Empresa ou ID inválido para atualização.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BusinessException("Nome da empresa é obrigatório.");
        }
        if (company.getAddress() == null || company.getAddress().trim().isEmpty()) {
            throw new BusinessException("Endereço da empresa é obrigatório.");
        }
        if (company.getPhone() == null || company.getPhone().trim().isEmpty()) {
            throw new BusinessException("Telefone da empresa é obrigatório.");
        }
        if (!validator.isValidEmail(company.getEmail())) {
            throw new BusinessException("Email da empresa inválido.");
        }

        try {
            Optional<Company> existingCompany = companyDAO.findById(company.getId());
            if (!existingCompany.isPresent()) {
                throw new BusinessException("Empresa com ID " + company.getId() + " não encontrada para atualização.");
            }

            Optional<Company> companyByCnpj = companyDAO.findByCnpj(company.getCnpj());
            if (companyByCnpj.isPresent() && companyByCnpj.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa com este CNPJ.");
            }

            company.setUpdatedAt(LocalDateTime.now());
            boolean updated = companyDAO.update(company);
            if (updated) {
                LOGGER.log(Level.INFO, "Empresa atualizada com sucesso: ID {0}", company.getId());
                return company; // Retorna o objeto Company atualizado
            } else {
                LOGGER.log(Level.WARNING, "Falha ao atualizar empresa. Nenhuma linha afetada.");
                throw new BusinessException("Falha ao atualizar empresa. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteCompany(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da empresa inválido para exclusão.");
        }
        try {
            Optional<Company> existingCompany = companyDAO.findById(id);
            if (!existingCompany.isPresent()) {
                throw new BusinessException("Empresa com ID " + id + " não encontrada para exclusão.");
            }
            boolean deleted = companyDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Empresa com ID {0} deletada com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar empresa com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new BusinessException("CNPJ não pode ser nulo ou vazio.");
        }
        if (!validator.isValidCnpj(cnpj)) {
            throw new BusinessException("Formato de CNPJ inválido.");
        }
        try {
            return companyDAO.findByCnpj(cnpj);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por CNPJ: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar empresa por CNPJ. Tente novamente mais tarde.", e);
        }
    }
}
