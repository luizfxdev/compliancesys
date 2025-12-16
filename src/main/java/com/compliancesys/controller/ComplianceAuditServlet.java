package com.compliancesys.controller;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.ComplianceAuditService;
import com.compliancesys.service.impl.ComplianceAuditServiceImpl;
import com.compliancesys.util.ConnectionFactory;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.HikariCPConnectionFactory;
import com.compliancesys.util.impl.ValidatorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/compliance/*")
public class ComplianceAuditServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComplianceAuditServlet.class.getName());
    private ConnectionFactory connectionFactory;
    private GsonUtil gsonUtil;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.connectionFactory = new HikariCPConnectionFactory();
            this.gsonUtil = new GsonUtilImpl();
            this.validator = new ValidatorImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar ComplianceAuditServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar ComplianceAuditServlet", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.connectionFactory != null) {
            this.connectionFactory.closeDataSource();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = connectionFactory.getConnection()) {
            ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            ComplianceAuditService complianceAuditService = new ComplianceAuditServiceImpl(complianceAuditDAO, journeyDAO, driverDAO, validator);

            String pathInfo = request.getPathInfo();
            String driverIdParam = request.getParameter("driverId");
            String journeyIdParam = request.getParameter("journeyId");
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            String statusParam = request.getParameter("status");

            if (pathInfo == null || pathInfo.equals("/")) {
                if (driverIdParam != null) {
                    int driverId = Integer.parseInt(driverIdParam);
                    List<ComplianceAudit> audits = complianceAuditService.getAuditsByDriverId(driverId);
                    out.print(gsonUtil.serialize(audits));
                } else if (journeyIdParam != null) {
                    int journeyId = Integer.parseInt(journeyIdParam);
                    List<ComplianceAudit> audits = complianceAuditService.getAuditsByJourneyId(journeyId);
                    out.print(gsonUtil.serialize(audits));
                } else if (startDateParam != null && endDateParam != null) {
                    LocalDate startDate = LocalDate.parse(startDateParam);
                    LocalDate endDate = LocalDate.parse(endDateParam);
                    List<ComplianceAudit> audits = complianceAuditService.getAuditsByAuditDateRange(startDate, endDate);
                    out.print(gsonUtil.serialize(audits));
                } else if (statusParam != null) {
                    ComplianceStatus status = ComplianceStatus.valueOf(statusParam.toUpperCase());
                    List<ComplianceAudit> audits = complianceAuditService.getAuditsByStatus(status);
                    out.print(gsonUtil.serialize(audits));
                } else {
                    List<ComplianceAudit> audits = complianceAuditService.getAllAudits();
                    out.print(gsonUtil.serialize(audits));
                }
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<ComplianceAudit> audit = complianceAuditService.getAuditById(id);
                if (audit.isPresent()) {
                    out.print(gsonUtil.serialize(audit.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonUtil.serialize(new ErrorResponse("Auditoria não encontrada")));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID ou parâmetro numérico inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID ou parâmetro numérico inválido")));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Parâmetro de status ou data inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("Parâmetro de status ou data inválido")));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = connectionFactory.getConnection()) {
            ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            ComplianceAuditService complianceAuditService = new ComplianceAuditServiceImpl(complianceAuditDAO, journeyDAO, driverDAO, validator);

            ComplianceAudit audit = gsonUtil.deserialize(request.getReader(), ComplianceAudit.class);
            ComplianceAudit createdAudit = complianceAuditService.createAudit(audit);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonUtil.serialize(createdAudit));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro ao auditar jornada")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("URL inválida para PUT")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            ComplianceAuditService complianceAuditService = new ComplianceAuditServiceImpl(complianceAuditDAO, journeyDAO, driverDAO, validator);

            int auditId = Integer.parseInt(pathInfo.substring(1));
            ComplianceAudit audit = gsonUtil.deserialize(request.getReader(), ComplianceAudit.class);
            audit.setId(auditId);

            boolean updated = complianceAuditService.updateAudit(audit);

            if (updated) {
                out.print(gsonUtil.serialize(audit));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Auditoria não encontrada")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID da auditoria inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro ao atualizar auditoria")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("URL inválida para DELETE")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            ComplianceAuditService complianceAuditService = new ComplianceAuditServiceImpl(complianceAuditDAO, journeyDAO, driverDAO, validator);

            int auditId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = complianceAuditService.deleteAudit(auditId);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Auditoria não encontrada")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID da auditoria inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro ao deletar auditoria")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    private static class ErrorResponse {
        private final String message;
        private final LocalDateTime timestamp;

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