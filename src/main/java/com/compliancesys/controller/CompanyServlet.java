// src/main/java/com/compliancesys/controller/CompanyServlet.java
package com.compliancesys.controller;

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
import com.google.gson.Gson; // Importar Gson para o ErrorResponse

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/companies/*")
public class CompanyServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CompanyServlet.class.getName());
    private CompanyService companyService;
    private GsonUtil gson; // Usar a interface GsonUtil
    private Connection connection; // Adicionar campo para a conexão

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Inicializa a conexão com o banco de dados. Em um ambiente de produção,
            // você usaria um DataSource gerenciado por um servidor de aplicações.
            // Para este projeto, vamos simular com DriverManager.
            Class.forName("org.h2.Driver"); // Ou o driver do seu banco de dados
            this.connection = DriverManager.getConnection("jdbc:h2:mem:compliancesys;DB_CLOSE_DELAY=-1", "sa", ""); // Ajuste conforme seu banco

            CompanyDAO companyDAO = new CompanyDAOImpl(connection);
            Validator validator = new ValidatorImpl();
            this.companyService = new CompanyServiceImpl(companyDAO, validator);
            this.gson = new GsonUtilImpl();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar CompanyServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar CompanyServlet", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao fechar conexão do banco de dados no CompanyServlet: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Buscar todas as empresas
                List<Company> companies = companyService.getAllCompanies();
                out.print(gson.serialize(companies));
            } else {
                // Buscar empresa por ID
                String idStr = pathInfo.substring(1); // Remove a barra inicial
                if (idStr.matches("\\d+")) { // Verifica se é um número
                    int id = Integer.parseInt(idStr);
                    Optional<Company> companyOptional = companyService.getCompanyById(id);
                    if (companyOptional.isPresent()) {
                        out.print(gson.serialize(companyOptional.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Empresa não encontrada")));
                    }
                } else if (idStr.startsWith("cnpj/")) { // Ex: /api/companies/cnpj/12345678901234
                    String cnpj = idStr.substring("cnpj/".length());
                    Optional<Company> companyOptional = companyService.getCompanyByCnpj(cnpj);
                    if (companyOptional.isPresent()) {
                        out.print(gson.serialize(companyOptional.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Empresa não encontrada por CNPJ")));
                    }
                } else if (idStr.startsWith("legalname/")) { // Ex: /api/companies/legalname/RazaoSocial
                    String legalName = idStr.substring("legalname/".length());
                    Optional<Company> companyOptional = companyService.getCompanyByLegalName(legalName);
                    if (companyOptional.isPresent()) {
                        out.print(gson.serialize(companyOptional.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Empresa não encontrada por Razão Social")));
                    }
                } else if (idStr.startsWith("tradingname/")) { // Ex: /api/companies/tradingname/NomeFantasia
                    String tradingName = idStr.substring("tradingname/".length());
                    Optional<Company> companyOptional = companyService.getCompanyByTradingName(tradingName);
                    if (companyOptional.isPresent()) {
                        out.print(gson.serialize(companyOptional.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Empresa não encontrada por Nome Fantasia")));
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.serialize(new ErrorResponse("Formato de URL inválido")));
                }
            }
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao buscar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Company newCompany = gson.deserialize(request.getReader(), Company.class);
            Company created = companyService.createCompany(newCompany); // Chamada corrigida
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(created));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da empresa é obrigatório para atualização")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            Company updatedCompany = gson.deserialize(request.getReader(), Company.class);
            updatedCompany.setId(id); // Garante que o ID do objeto corresponde ao ID da URL

            Company result = companyService.updateCompany(updatedCompany);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.serialize(result));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID é obrigatório para exclusão")));
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
                out.print(gson.serialize(new ErrorResponse("Empresa não encontrada")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    // Classe interna para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;
        private LocalDateTime timestamp;

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
}
