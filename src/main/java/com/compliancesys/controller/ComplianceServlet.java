package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.ComplianceReport;
import com.compliancesys.service.ComplianceService;
import com.compliancesys.service.impl.ComplianceServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

@WebServlet("/compliance/*")
public class ComplianceServlet extends HttpServlet {

    private ComplianceService complianceService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl();
        JourneyDAO journeyDAO = new JourneyDAOImpl();
        Validator validator = new ValidatorImpl();
        this.complianceService = new ComplianceServiceImpl(complianceAuditDAO, journeyDAO, validator);
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
                List<ComplianceAudit> audits = complianceService.getAllComplianceAudits();
                out.print(gsonSerializer.serialize(audits));
            } else if (pathInfo.startsWith("/journey/")) {
                int journeyId = Integer.parseInt(pathInfo.substring("/journey/".length()));
                List<ComplianceAudit> audits = complianceService.getComplianceAuditsByJourneyId(journeyId);
                out.print(gsonSerializer.serialize(audits));
            } else if (pathInfo.startsWith("/report/driver/")) {
                int driverId = Integer.parseInt(pathInfo.substring("/report/driver/".length()));
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");

                if (startDateParam == null || endDateParam == null || startDateParam.isEmpty() || endDateParam.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Parâmetros startDate e endDate são obrigatórios.")));
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);
                ComplianceReport report = complianceService.generateDriverComplianceReport(driverId, startDate, endDate);
                out.print(gsonSerializer.serialize(report));
            } else if (pathInfo.startsWith("/report/overall")) {
                String startDateParam = request.getParameter("startDate");
                String endDateParam = request.getParameter("endDate");

                if (startDateParam == null || endDateParam == null || startDateParam.isEmpty() || endDateParam.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Parâmetros startDate e endDate são obrigatórios.")));
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);
                List<ComplianceAudit> report = complianceService.generateOverallComplianceReport(startDate, endDate);
                out.print(gsonSerializer.serialize(report));
            } else {
                int auditId = Integer.parseInt(pathInfo.substring(1));
                Optional<ComplianceAudit> audit = complianceService.getComplianceAuditById(auditId);
                if (audit.isPresent()) {
                    out.print(gsonSerializer.serialize(audit.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Auditoria não encontrada.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("Use /compliance/audit para realizar auditoria.")));
            out.flush();
            return;
        }

        try {
            String requestBody = request.getReader().readLine();
            AuditRequest auditRequest = gsonSerializer.deserialize(requestBody, AuditRequest.class);
            int journeyId = auditRequest.getJourneyId();

            ComplianceAudit audit = complianceService.performComplianceAudit(journeyId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(new SuccessResponse("Auditoria realizada. ID: " + audit.getId())));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da jornada inválido.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados inválidos: " + e.getMessage())));
        }
        out.flush();
    }

    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }

    private static class SuccessResponse {
        private String message;
        public SuccessResponse(String message) { this.message = message; }
    }

    private static class AuditRequest {
        private int journeyId;
        public int getJourneyId() { return journeyId; }
        public void setJourneyId(int journeyId) { this.journeyId = journeyId; }
    }
}