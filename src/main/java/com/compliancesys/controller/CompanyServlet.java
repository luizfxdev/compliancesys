package com.compliancesys.controller;

import com.compliancesys.model.Company;
import com.compliancesys.service.CompanyService;
import com.compliancesys.service.impl.CompanyServiceImpl; // Assumindo uma implementação
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.impl.GsonUtilImpl; // Assumindo uma implementação
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para gerenciar operações CRUD de empresas (Company).
 * Responde a requisições HTTP para /companies.
 */
@WebServlet("/companies/*") // Adicionado /* para permitir pathInfo
public class CompanyServlet extends HttpServlet {

    private CompanyService companyService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.companyService = new CompanyServiceImpl(); // Você precisará criar CompanyServiceImpl
        this.gsonSerializer = new GsonUtilImpl(); // Você precisará criar GsonUtilImpl
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /companies/{id}

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /companies - Retorna todas as empresas
                List<Company> companies = companyService.getAllCompanies();
                out.print(gsonSerializer.serialize(companies));
            } else {
                // GET /companies/{id} - Retorna uma empresa específica
                int companyId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
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
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
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
            int newCompanyId = companyService.registerCompany(company);
            company.setId(newCompanyId); // Define o ID gerado no objeto
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(company));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar empresa: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados da empresa inválidos: " + e.getMessage())));
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
            company.setId(companyId); // Garante que o ID do path seja usado

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
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar empresa: " + e.getMessage())));
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
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Empresa não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da empresa inválido.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar empresa: " + e.getMessage())));
        }
        out.flush();
    }

    // Classes auxiliares para padronizar respostas de erro/sucesso
    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }
    // Não é necessário SuccessResponse para este servlet, mas pode ser adicionado se houver necessidade de mensagens de sucesso explícitas.
}
