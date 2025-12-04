package com.compliancesys.controller;

import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.service.ComplianceService;
import com.compliancesys.service.impl.ComplianceServiceImpl; // Assumindo uma implementação
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para gerenciar operações de auditoria e relatórios de conformidade.
 * Responde a requisições HTTP para /compliance.
 */
@WebServlet("/compliance/*") // Adicionado /* para permitir pathInfo
public class ComplianceServlet extends HttpServlet {

    private ComplianceService complianceService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.complianceService = new ComplianceServiceImpl(); // Você precisará criar ComplianceServiceImpl
        this.gsonSerializer = new GsonUtilImpl(); // Você precisará criar GsonUtilImpl
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // Ex: /compliance/{auditId}, /compliance/journey/{journeyId}, /compliance/report/driver/{driverId}?start=...&end=...

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /compliance - Retorna todas as auditorias de conformidade
                List<ComplianceAudit> audits = complianceService.getAllComplianceAudits();
                out.print(gsonSerializer.serialize(audits));
            } else if (pathInfo.startsWith("/journey/")) {
                // GET /compliance/journey/{journeyId} - Retorna auditorias para uma jornada específica
                int journeyId = Integer.parseInt(pathInfo.substring("/journey/".length()));
                List<ComplianceAudit> audits = complianceService.getComplianceAuditsByJourneyId(journeyId);
                out.print(gsonSerializer.serialize(audits));
            } else if (pathInfo.startsWith("/report/driver/")) {
                // GET /compliance/report/driver/{driverId}?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
                int driverId = Integer.parseInt(pathInfo.substring("/report/driver/".length()));
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");

                if (startDateParam == null || endDateParam == null || startDateParam.isEmpty() || endDateParam.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Parâmetros startDate e endDate são obrigatórios para o relatório de motorista.")));
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);
                List<ComplianceAudit> report = complianceService.generateDriverComplianceReport(driverId, startDate, endDate);
                out.print(gsonSerializer.serialize(report));
            } else if (pathInfo.startsWith("/report/overall")) {
                // GET /compliance/report/overall?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");

                if (startDateParam == null || endDateParam == null || startDateParam.isEmpty() || endDateParam.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Parâmetros startDate e endDate são obrigatórios para o relatório geral.")));
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);
                List<ComplianceAudit> report = complianceService.generateOverallComplianceReport(startDate, endDate);
                out.print(gsonSerializer.serialize(report));
            } else {
                // GET /compliance/{auditId} - Retorna uma auditoria específica
                int auditId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<ComplianceAudit> audit = complianceService.getComplianceAuditById(auditId);
                if (audit.isPresent()) {
                    out.print(gsonSerializer.serialize(audit.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Auditoria de conformidade não encontrada.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
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

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || !pathInfo.equals("/audit")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Requisição POST inválida. Use /compliance/audit para realizar uma auditoria.")));
            out.flush();
            return;
        }

        try {
            // Espera um JSON com o ID da jornada para auditar
            // Ex: {"journeyId": 123}
            String requestBody = request.getReader().readLine();
            // Melhor desserializar para um POJO específico para a requisição
            AuditRequest auditRequest = gsonSerializer.deserialize(requestBody, AuditRequest.class);
            int journeyId = auditRequest.getJourneyId();

            int newAuditId = complianceService.performComplianceAudit(journeyId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(new SuccessResponse("Auditoria de conformidade realizada com sucesso. ID: " + newAuditId)));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da jornada inválido no corpo da requisição.")));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao realizar auditoria de conformidade: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de requisição inválidos: " + e.getMessage())));
        }
        out.flush();
    }

    // Classe auxiliar para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }

    // Classe auxiliar para padronizar respostas de sucesso (se necessário)
    private static class SuccessResponse {
        private String message;
        public SuccessResponse(String message) { this.message = message; }
    }

    // POJO para desserializar a requisição de auditoria
    private static class AuditRequest {
        private int journeyId;
        public int getJourneyId() { return journeyId; }
        public void setJourneyId(int journeyId) { this.journeyId = journeyId; }
    }
}
