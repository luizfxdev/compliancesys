package com.compliancesys.controller;

<<<<<<< Updated upstream
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
=======
import com.compliancesys.config.DatabaseConfig; // Importa o DatabaseConfig
import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl; // Assumindo que você terá essa implementação
import com.compliancesys.exception.BusinessException; // Importa BusinessException
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.service.impl.CompanyServiceImpl; // Assumindo que você terá essa implementação
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator; // Importa Validator
import com.compliancesys.util.impl.GsonUtilImpl; // Assumindo que você terá essa implementação
import com.compliancesys.util.impl.ValidatorImpl; // Assumindo que você terá essa implementação
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource; // Importa DataSource
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime; // Importa LocalDateTime para ErrorResponse
import java.util.List;
import java.util.Optional;
import java.util.logging.Level; // Importa Level para logging
import java.util.logging.Logger; // Importa Logger para logging
import java.util.stream.Collectors; // Importa Collectors para ler o corpo da requisição
>>>>>>> Stashed changes

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.service.impl.CompanyServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/companies/*")
public class CompanyServlet extends HttpServlet {

<<<<<<< Updated upstream
    private static final Logger LOGGER = Logger.getLogger(CompanyServlet.class.getName());
    private CompanyService companyService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
        CompanyDAO companyDAO = new CompanyDAOImpl();
        Validator validator = new ValidatorImpl();
        this.companyService = new CompanyServiceImpl(companyDAO, validator);
        this.gson = new GsonUtilImpl();
=======
    private static final Logger LOGGER = Logger.getLogger(CompanyServlet.class.getName()); // Logger para a classe
    private CompanyService companyService;
    private GsonUtil gsonSerializer;
    private Validator validator; // Adiciona o Validator

    @Override
    public void init() throws ServletException {
        try {
            // Obtém o DataSource do nosso Singleton DatabaseConfig
            DataSource dataSource = DatabaseConfig.getInstance().getDataSource();

            // Instancia as DAOs, passando o DataSource
            CompanyDAO companyDAO = new CompanyDAOImpl(dataSource); // Construtor de CompanyDAOImpl deve aceitar DataSource

            // Instancia o Validator
            this.validator = new ValidatorImpl(); // Assumindo que ValidatorImpl não precisa de DataSource

            // Instancia o Service, passando as DAOs e Validator
            this.companyService = new CompanyServiceImpl(companyDAO, validator); // Construtor de CompanyServiceImpl deve aceitar CompanyDAO e Validator

            // Instancia o GsonUtil
            this.gsonSerializer = new GsonUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar CompanyServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar CompanyServlet", e);
        }
>>>>>>> Stashed changes
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

<<<<<<< Updated upstream
        String pathInfo = request.getPathInfo();

=======
        String pathInfo = request.getPathInfo(); // /companies/{id}
>>>>>>> Stashed changes
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /companies - Retorna todas as empresas
                List<Company> companies = companyService.getAllCompanies();
                out.print(gson.serialize(companies));
            } else if (pathInfo.startsWith("/cnpj/")) {
                // GET /companies/cnpj/{cnpj}
                String cnpj = pathInfo.substring("/cnpj/".length());
                Optional<Company> company = companyService.getCompanyByCnpj(cnpj);
                if (company.isPresent()) {
                    out.print(gson.serialize(company.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Empresa não encontrada com CNPJ: " + cnpj)));
                }
            } else if (pathInfo.startsWith("/name/")) {
                // GET /companies/name/{name}
                String name = pathInfo.substring("/name/".length());
                // CORRIGIDO: getCompanyByName retorna Optional<Company>, não List
                Optional<Company> company = companyService.getCompanyByName(name);
                if (company.isPresent()) {
                    out.print(gson.serialize(company.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Empresa não encontrada com nome: " + name)));
                }
            } else {
                // GET /companies/{id} - Retorna uma empresa específica
                String idStr = pathInfo.substring(1);
                if (idStr.endsWith("/")) {
                    idStr = idStr.substring(0, idStr.length() - 1);
                }
                int companyId = Integer.parseInt(idStr);
                Optional<Company> company = companyService.getCompanyById(companyId);
                if (company.isPresent()) {
                    out.print(gson.serialize(company.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Empresa não encontrada com ID: " + companyId)));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de empresa inválido no GET: " + pathInfo + " - " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("ID de empresa inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
            LOGGER.log(Level.WARNING, "ID inválido no caminho da URL para GET Company: " + e.getMessage(), e);
        } catch (BusinessException e) { // Captura BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET Company: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET Company: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET Company: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
<<<<<<< Updated upstream
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Company newCompany = gson.deserialize(jsonBody, Company.class);
            // CORRIGIDO: Usando registerCompany em vez de createCompany
            Company createdCompany = companyService.registerCompany(newCompany);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdCompany));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            // Lê o corpo da requisição de forma robusta
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Company company = gsonSerializer.deserialize(jsonBody, Company.class);

            // Validação básica (se o Validator for usado aqui)
            if (!validator.isValidCnpj(company.getCnpj())) {
                throw new BusinessException("CNPJ inválido.");
            }
            if (!validator.isValidName(company.getLegalName())) {
                throw new BusinessException("Nome legal inválido.");
            }

            Company createdCompany = companyService.registerCompany(company); // Assumindo que registerCompany retorna a Company criada
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(createdCompany));
        } catch (BusinessException e) { // Captura BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar empresa: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar empresa: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar empresa: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Alterado para INTERNAL_SERVER_ERROR para erros inesperados
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar empresa: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar empresa: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da empresa é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
<<<<<<< Updated upstream
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Company updatedCompany = gson.deserialize(jsonBody, Company.class);
            updatedCompany.setId(id);

            Company result = companyService.updateCompany(updatedCompany);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.serialize(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da empresa inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar empresa: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            int companyId = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Company company = gsonSerializer.deserialize(jsonBody, Company.class);
            company.setId(companyId); // Garante que o ID do path seja usado

            // Validação básica (se o Validator for usado aqui)
            if (!validator.isValidCnpj(company.getCnpj())) {
                throw new BusinessException("CNPJ inválido.");
            }
            if (!validator.isValidName(company.getLegalName())) {
                throw new BusinessException("Nome legal inválido.");
            }

            if (companyService.updateCompany(company)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(company));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Empresa não encontrada para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar empresa: " + e.getMessage(), e);
        } catch (BusinessException e) { // Captura BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar empresa: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor ao atualizar empresa: " + e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar empresa: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar empresa: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Alterado para INTERNAL_SERVER_ERROR para erros inesperados
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar empresa: " + e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar empresa: " + e.getMessage(), e);
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da empresa é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = companyService.deleteCompany(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Empresa não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("ID da empresa inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar empresa: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar empresa: " + e.getMessage(), e);
        } catch (BusinessException e) { // Captura BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar empresa: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao deletar empresa: " + e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar empresa: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Alterado para INTERNAL_SERVER_ERROR para erros inesperados
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar empresa: " + e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar empresa: " + e.getMessage(), e);
        } finally {
            out.flush();
        }
    }

    private static class ErrorResponse {
        private String message;
<<<<<<< Updated upstream
        private LocalDateTime timestamp;
=======
        private LocalDateTime timestamp; // Adicionado timestamp
>>>>>>> Stashed changes

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes
