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
    public Company registerCompany(Company company) throws BusinessException, SQLException {
        if (company == null) {
            throw new BusinessException("Dados da empresa não podem ser nulos.");
        }
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BusinessException("Nome da empresa é obrigatório.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
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
            if (companyDAO.findByName(company.getName()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este nome.");
            }
            if (companyDAO.findByEmail(company.getEmail()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este email.");
            }
            if (companyDAO.findByPhone(company.getPhone()).isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este telefone.");
            }

            company.setCreatedAt(LocalDateTime.now());
            company.setUpdatedAt(LocalDateTime.now());
            int id = companyDAO.create(company);
            if (id <= 0) {
                throw new BusinessException("Falha ao registrar empresa. Tente novamente.");
            }
            company.setId(id);
            LOGGER.log(Level.INFO, "Empresa registrada com sucesso: {0}", company.getName());
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
            throw new BusinessException("Erro de banco de dados ao buscar empresa por CNPJ. Tente novamente mais tarde.", e);
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
        if (company == null) {
            throw new BusinessException("Dados da empresa não podem ser nulos.");
        }
        if (!validator.isValidId(company.getId())) {
            throw new BusinessException("ID da empresa inválido para atualização.");
        }
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BusinessException("Nome da empresa é obrigatório.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
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
            Optional<Company> companyByCnpj = companyDAO.findByCnpj(company.getCnpj());
            if (companyByCnpj.isPresent() && companyByCnpj.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com este CNPJ.");
            }
            Optional<Company> companyByName = companyDAO.findByName(company.getName());
            if (companyByName.isPresent() && companyByName.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com este nome.");
            }
            Optional<Company> companyByEmail = companyDAO.findByEmail(company.getEmail());
            if (companyByEmail.isPresent() && companyByEmail.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com este email.");
            }
            Optional<Company> companyByPhone = companyDAO.findByPhone(company.getPhone());
            if (companyByPhone.isPresent() && companyByPhone.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com este telefone.");
            }

            company.setUpdatedAt(LocalDateTime.now());
            boolean updated = companyDAO.update(company);
            if (!updated) {
                throw new BusinessException("Falha ao atualizar empresa. Verifique os dados e tente novamente.");
            }
            LOGGER.log(Level.INFO, "Empresa atualizada com sucesso: {0}", company.getName());
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
            boolean deleted = companyDAO.delete(companyId);
            if (!deleted) {
                throw new BusinessException("Empresa não encontrada para exclusão.");
            }
            LOGGER.log(Level.INFO, "Empresa deletada com sucesso. ID: {0}", companyId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao deletar empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByName(String name) throws BusinessException, SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Nome da empresa não pode ser vazio para busca.");
        }
        try {
            return companyDAO.findByName(name);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por nome: " + e.getMessage(), e);
            throw new BusinessException("Erro de banco de dados ao buscar empresa por nome. Tente novamente mais tarde.", e);
        }
    }
}
