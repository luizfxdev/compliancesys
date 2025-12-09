package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.service.impl.CompanyServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator; // Import adicionado para SQLException
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

@WebServlet("/companies/*")
public class CompanyServlet extends HttpServlet {

    private CompanyService companyService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        CompanyDAO companyDAO = new CompanyDAOImpl();
        Validator validator = new ValidatorImpl();
        this.companyService = new CompanyServiceImpl(companyDAO, validator);
        this.gsonSerializer = new GsonUtilImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Company> companies = companyService.getAllCompanies();
                out.print(gsonSerializer.serialize(companies));
            } else {
                int companyId = Integer.parseInt(pathInfo.substring(1));
                Optional<Company> company = companyService.getCompanyById(companyId);
                if (company.isPresent()) {
                    out.print(gsonSerializer.serialize(company.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Empresa não encontrada.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro: " + e.getMessage())));
        } catch (SQLException e) { // <--- NOVO CATCH PARA SQLException
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados ao buscar empresa: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado: " + e.getMessage())));
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Company company = gsonSerializer.deserialize(request.getReader().readLine(), Company.class);
            Company registeredCompany = companyService.registerCompany(company);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(registeredCompany));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) { // <--- NOVO CATCH PARA SQLException
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados ao registrar empresa: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao registrar empresa: " + e.getMessage())));
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int companyId = Integer.parseInt(pathInfo.substring(1));
            Company company = gsonSerializer.deserialize(request.getReader().readLine(), Company.class);
            company.setId(companyId);

            Company updatedCompany = companyService.updateCompany(company);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonSerializer.serialize(updatedCompany));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa inválido.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) { // <--- NOVO CATCH PARA SQLException
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados ao atualizar empresa: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados da empresa inválidos: " + e.getMessage())));
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int companyId = Integer.parseInt(pathInfo.substring(1));
            if (companyService.deleteCompany(companyId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Empresa não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa inválido.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar empresa: " + e.getMessage())));
        } catch (SQLException e) { // <--- NOVO CATCH PARA SQLException
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados ao deletar empresa: " + e.getMessage())));
        }
        out.flush();
    }

    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
