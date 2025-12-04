package com.compliancesys.service.impl;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface CompanyService.
 * Contém a lógica de negócio para a entidade Company, interagindo com a camada DAO.
 */
public class CompanyServiceImpl implements CompanyService {

    private static final Logger LOGGER = Logger.getLogger(CompanyServiceImpl.class.getName());
    private final CompanyDAO companyDAO;

    /**
     * Construtor padrão que inicializa o CompanyDAOImpl.
     * Pode ser estendido para injeção de dependência em um ambiente de produção.
     */
    public CompanyServiceImpl() {
        this.companyDAO = new CompanyDAOImpl();
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param companyDAO A implementação de CompanyDAO a ser utilizada.
     */
    public CompanyServiceImpl(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    @Override
    public Company createCompany(Company company) throws BusinessException {
        // Validações de negócio antes de criar a empresa
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BusinessException("O nome da empresa não pode ser vazio.");
        }
        if (company.getCnpj() == null || company.getCnpj().trim().isEmpty()) {
            throw new BusinessException("O CNPJ da empresa não pode ser vazio.");
        }
        // Exemplo de validação de formato de CNPJ (simplificado)
        if (!company.getCnpj().matches("\\d{14}")) { // Apenas dígitos, 14 caracteres
            throw new BusinessException("O CNPJ deve conter 14 dígitos numéricos.");
        }

        try {
            // Verifica se já existe uma empresa com o mesmo CNPJ
            Optional<Company> existingCompany = companyDAO.findByCnpj(company.getCnpj());
            if (existingCompany.isPresent()) {
                throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ: " + company.getCnpj());
            }

            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            company.setCreatedAt(now);
            company.setUpdatedAt(now);

            int id = companyDAO.create(company);
            if (id > 0) {
                company.setId(id);
                LOGGER.log(Level.INFO, "Empresa criada com sucesso: {0}", company.getName());
                return company;
            } else {
                throw new BusinessException("Falha ao criar a empresa. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar a empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da empresa deve ser um valor positivo.");
        }
        try {
            return companyDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Company> getCompanyByCnpj(String cnpj) throws BusinessException {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new BusinessException("O CNPJ da empresa não pode ser vazio para a busca.");
        }
        try {
            return companyDAO.findByCnpj(cnpj);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa por CNPJ: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a empresa. Tente novamente mais tarde.", e);
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
        if (company.getId() <= 0) {
            throw new BusinessException("O ID da empresa deve ser um valor positivo para atualização.");
        }
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new BusinessException("O nome da empresa não pode ser vazio.");
        }
        if (company.getCnpj() == null || company.getCnpj().trim().isEmpty()) {
            throw new BusinessException("O CNPJ da empresa não pode ser vazio.");
        }
        if (!company.getCnpj().matches("\\d{14}")) {
            throw new BusinessException("O CNPJ deve conter 14 dígitos numéricos.");
        }

        try {
            // Verifica se a empresa a ser atualizada existe
            Optional<Company> existingCompanyById = companyDAO.findById(company.getId());
            if (existingCompanyById.isEmpty()) {
                throw new BusinessException("Empresa com ID " + company.getId() + " não encontrada para atualização.");
            }

            // Verifica se o novo CNPJ já pertence a outra empresa (que não seja a própria)
            Optional<Company> existingCompanyByCnpj = companyDAO.findByCnpj(company.getCnpj());
            if (existingCompanyByCnpj.isPresent() && existingCompanyByCnpj.get().getId() != company.getId()) {
                throw new BusinessException("Já existe outra empresa cadastrada com este CNPJ: " + company.getCnpj());
            }

            // Define a data de atualização
            company.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            company.setCreatedAt(existingCompanyById.get().getCreatedAt());

            boolean updated = companyDAO.update(company);
            if (updated) {
                LOGGER.log(Level.INFO, "Empresa atualizada com sucesso: {0}", company.getName());
                return company;
            } else {
                throw new BusinessException("Falha ao atualizar a empresa. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar a empresa. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteCompany(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da empresa deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se a empresa existe antes de tentar deletar
            Optional<Company> existingCompany = companyDAO.findById(id);
            if (existingCompany.isEmpty()) {
                throw new BusinessException("Empresa com ID " + id + " não encontrada para exclusão.");
            }
            // Adicionar lógica para verificar dependências (ex: se há motoristas ou veículos associados)
            // Se houver, lançar BusinessException ou tratar a exclusão em cascata (se o DB permitir e for desejado)

            boolean deleted = companyDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Empresa com ID {0} deletada com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar empresa com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar a empresa. Tente novamente mais tarde.", e);
        }
    }
}
