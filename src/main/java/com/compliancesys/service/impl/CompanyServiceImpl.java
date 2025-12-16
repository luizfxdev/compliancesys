package com.compliancesys.service.impl;

import java.sql.SQLException;
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
    public Company createCompany(Company company) throws SQLException, BusinessException {
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }
        if (companyDAO.findByCnpj(company.getCnpj()).isPresent()) {
            throw new BusinessException("CNPJ já cadastrado.");
        }
        if (companyDAO.findByLegalName(company.getLegalName()).isPresent()) {
            throw new BusinessException("Razão Social já cadastrada.");
        }
        if (companyDAO.findByTradingName(company.getTradingName()).isPresent()) {
            throw new BusinessException("Nome Fantasia já cadastrado.");
        }

        int id = companyDAO.create(company);
        company.setId(id);
        LOGGER.log(Level.INFO, "Empresa criada com ID: {0}", id);
        return company;
    }

    @Override
    public Company updateCompany(Company company) throws SQLException, BusinessException {
        if (company.getId() <= 0) {
            throw new BusinessException("ID da empresa inválido para atualização.");
        }
        if (!validator.isValidCnpj(company.getCnpj())) {
            throw new BusinessException("CNPJ inválido.");
        }

        Optional<Company> existingCompanyByCnpj = companyDAO.findByCnpj(company.getCnpj());
        if (existingCompanyByCnpj.isPresent() && existingCompanyByCnpj.get().getId() != company.getId()) {
            throw new BusinessException("CNPJ já cadastrado para outra empresa.");
        }

        Optional<Company> existingCompanyByLegalName = companyDAO.findByLegalName(company.getLegalName());
        if (existingCompanyByLegalName.isPresent() && existingCompanyByLegalName.get().getId() != company.getId()) {
            throw new BusinessException("Razão Social já cadastrada para outra empresa.");
        }

        Optional<Company> existingCompanyByTradingName = companyDAO.findByTradingName(company.getTradingName());
        if (existingCompanyByTradingName.isPresent() && existingCompanyByTradingName.get().getId() != company.getId()) {
            throw new BusinessException("Nome Fantasia já cadastrado para outra empresa.");
        }

        if (!companyDAO.update(company)) {
            throw new BusinessException("Empresa não encontrada para atualização.");
        }
        LOGGER.log(Level.INFO, "Empresa atualizada com ID: {0}", company.getId());
        return company;
    }

    @Override
    public boolean deleteCompany(int id) throws SQLException, BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID da empresa inválido para exclusão.");
        }
        boolean deleted = companyDAO.delete(id);
        if (deleted) {
            LOGGER.log(Level.INFO, "Empresa deletada com ID: {0}", id);
        } else {
            LOGGER.log(Level.WARNING, "Empresa com ID {0} não encontrada para exclusão.", id);
        }
        return deleted;
    }

    @Override
    public Optional<Company> getCompanyById(int id) throws SQLException {
        return companyDAO.findById(id);
    }

    @Override
    public List<Company> getAllCompanies() throws SQLException {
        return companyDAO.findAll();
    }

    @Override
    public Optional<Company> getCompanyByCnpj(String cnpj) throws SQLException {
        return companyDAO.findByCnpj(cnpj);
    }

    @Override
    public Optional<Company> getCompanyByLegalName(String legalName) throws SQLException {
        return companyDAO.findByLegalName(legalName);
    }

    @Override
    public Optional<Company> getCompanyByTradingName(String tradingName) throws SQLException {
        return companyDAO.findByTradingName(tradingName);
    }
}
