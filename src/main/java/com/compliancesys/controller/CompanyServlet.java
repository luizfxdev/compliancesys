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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/companies/*")
public class CompanyServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CompanyServlet.class.getName());
    private CompanyService companyService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
        CompanyDAO companyDAO = new CompanyDAOImpl();
        Validator validator = new ValidatorImpl();
        this.companyService = new CompanyServiceImpl(companyDAO, validator);
        this.gson = new GsonUtilImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

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
            out.print(gson.serialize(new ErrorResponse("ID de empresa inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
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
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar empresa: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor ao atualizar empresa: " + e.getMessage())));
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
            out.print(gson.serialize(new ErrorResponse("ID da empresa inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar empresa: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar empresa: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar empresa: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao deletar empresa: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar empresa: " + e.getMessage(), e);
        } finally {
            out.flush();
        }
    }

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