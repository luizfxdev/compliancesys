package com.compliancesys.service.impl;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.util.Validator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompanyServiceImpl implements CompanyService {

    private static final Logger LOGGER = Logger.getLogger(CompanyServiceImpl.class.getName());
    private final CompanyDAO companyDAO;
    private final Validator validator;

    public CompanyServiceImpl(CompanyDAO companyDAO, Validator validator) {
        this.companyDAO = companyDAO;
        this.validator = validator;
    }

    @Override
    public Company registerCompany(Company company) throws BusinessException {
        if (company == null) {
            throw new BusinessException("Empresa não pode ser nula.");
        }
        if (!validator.isValidName(company.getName())) {
            throw new BusinessException("Nome da empresa inválido.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        if (!validator.isValidAddress(company.getAddress())) {
            throw new BusinessException("Endereço inválido.");
        }

        try {
            Optional<Company> existingCompany = companyDAO.findByCnpj(company.getCnpj());
            if (existingCompany.isPresent()) {
                throw new BusinessException("Empresa com CNPJ " + company.getCnpj() + " já cadastrada.");
            }

            company.setCreatedAt(LocalDateTime.now());
            company.setUpdatedAt(LocalDateTime.now());
            int id = companyDAO.create(company);
            company.setId(id);
            LOGGER.log(Level.INFO, "Empresa registrada com sucesso: ID {0}", id);
            return company;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao registrar empresa.", e);
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
            LOGGER.log(Level.SEVERE, "Erro ao buscar empresa por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar empresa.", e);
        }
    }

    @Override
    public List<Company> getAllCompanies() throws BusinessException {
        try {
            return companyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar todas as empresas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar empresas.", e);
        }
    }

    @Override
    public Company updateCompany(Company company) throws BusinessException {
        if (company == null || company.getId() <= 0) {
            throw new BusinessException("Empresa ou ID inválido para atualização.");
        }
        if (!validator.isValidName(company.getName())) {
            throw new BusinessException("Nome da empresa inválido.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        if (!validator.isValidAddress(company.getAddress())) {
            throw new BusinessException("Endereço inválido.");
        }

        try {
            Optional<Company> existingCompany = companyDAO.findById(company.getId());
            if (existingCompany.isEmpty()) {
                throw new BusinessException("Empresa com ID " + company.getId() + " não encontrada.");
            }

            // O CNPJ não deve ser alterado para uma empresa existente, ou se for, deve ser validado para não duplicar
            // Se o CNPJ for alterado, verificar se o novo CNPJ já existe para outra empresa
            if (!existingCompany.get().getCnpj().equals(company.getCnpj())) {
                Optional<Company> companyWithNewCnpj = companyDAO.findByCnpj(company.getCnpj());
                if (companyWithNewCnpj.isPresent() && companyWithNewCnpj.get().getId() != company.getId()) {
                    throw new BusinessException("Já existe outra empresa com o CNPJ " + company.getCnpj() + ".");
                }
            }

            company.setUpdatedAt(LocalDateTime.now());
            // Mantém o createdAt original
            company.setCreatedAt(existingCompany.get().getCreatedAt());

            boolean updated = companyDAO.update(company);
            if (updated) {
                LOGGER.log(Level.INFO, "Empresa atualizada com sucesso: ID {0}", company.getId());
                return company;
            } else {
                throw new BusinessException("Falha ao atualizar empresa.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar empresa.", e);
        }
    }

    @Override
    public boolean deleteCompany(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da empresa inválido para exclusão.");
        }
        try {
            Optional<Company> existingCompany = companyDAO.findById(id);
            if (existingCompany.isEmpty()) {
                throw new BusinessException("Empresa com ID " + id + " não encontrada.");
            }

            boolean deleted = companyDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Empresa deletada com sucesso: ID {0}", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar empresa.", e);
        }
    }

    // MÉTODO ADICIONADO PARA RESOLVER O ERRO DE COMPILAÇÃO
    @Override
    public Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException {
        if (!validator.isValidCnpj(cnpj)) {
            throw new BusinessException("CNPJ inválido.");
        }
        try {
            return companyDAO.findByCnpj(cnpj);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar empresa por CNPJ: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar empresa por CNPJ.", e);
        }
    }
}
