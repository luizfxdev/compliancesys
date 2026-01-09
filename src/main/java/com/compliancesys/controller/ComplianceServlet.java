package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.impl.ComplianceAuditServiceImpl;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.TimeUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

@WebServlet("/compliance/*")
public class ComplianceServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServlet.class.getName());
    private ComplianceAuditServiceImpl complianceAuditService;
    private JourneyDAOImpl journeyDAO;
    private TimeRecordDAOImpl timeRecordDAO;
    private GsonUtilImpl gson;

    @Override
    public void init() throws ServletException {
        try {
            Connection connection = DatabaseConfig.getInstance().getConnection();
            
            ComplianceAuditDAOImpl complianceAuditDAO = new ComplianceAuditDAOImpl(connection);
            JourneyDAOImpl journeyDAO = new JourneyDAOImpl(connection);
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            MobileCommunicationDAOImpl mobileCommunicationDAO = new MobileCommunicationDAOImpl(connection);
            DriverDAOImpl driverDAO = new DriverDAOImpl(connection);
            
            ValidatorImpl validator = new ValidatorImpl();
            TimeUtilImpl timeUtil = new TimeUtilImpl();
            
            this.complianceAuditService = new ComplianceAuditServiceImpl(
                complianceAuditDAO, journeyDAO, timeRecordDAO, mobileCommunicationDAO,
                driverDAO, validator, timeUtil
            );
            this.journeyDAO = journeyDAO;
            this.timeRecordDAO = timeRecordDAO;
            this.gson = new GsonUtilImpl();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar ComplianceServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar ComplianceServlet", e);
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
                // GET /compliance - Lista todas as auditorias
                List<ComplianceAudit> audits = complianceAuditService.getAllAudits();
                out.print(gson.serialize(audits));
            } else if (pathInfo.startsWith("/journey/")) {
                // GET /compliance/journey/{id} - Lista auditorias de uma jornada
                int journeyId = Integer.parseInt(pathInfo.substring("/journey/".length()));
                List<ComplianceAudit> audits = complianceAuditService.getAuditsByJourneyId(journeyId);
                out.print(gson.serialize(audits));
            } else if (pathInfo.startsWith("/driver/")) {
                // GET /compliance/driver/{id} - Lista auditorias de um motorista
                int driverId = Integer.parseInt(pathInfo.substring("/driver/".length()));
                List<ComplianceAudit> audits = complianceAuditService.getAuditsByDriverId(driverId);
                out.print(gson.serialize(audits));
            } else if (pathInfo.equals("/date-range")) {
                // GET /compliance/date-range?start=YYYY-MM-DD&end=YYYY-MM-DD
                String startDateParam = request.getParameter("start");
                String endDateParam = request.getParameter("end");

                if (startDateParam == null || endDateParam == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.serialize(new ErrorResponse("Parâmetros 'start' e 'end' são obrigatórios")));
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);

                List<ComplianceAudit> audits = complianceAuditService.getAuditsByDateRange(startDate, endDate);
                out.print(gson.serialize(audits));
            } else {
                // GET /compliance/{id} - Busca auditoria por ID
                int auditId = Integer.parseInt(pathInfo.substring(1));
                Optional<ComplianceAudit> auditOpt = complianceAuditService.getAuditById(auditId);
                
                if (auditOpt.isPresent()) {
                    out.print(gson.serialize(auditOpt.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Auditoria não encontrada")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID ou parâmetro numérico inválido")));
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD")));
            LOGGER.log(Level.WARNING, "Formato de data inválido: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
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

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.startsWith("/audit/journey/")) {
                // POST /compliance/audit/journey/{id} - Realizar auditoria de uma jornada
                int journeyId = Integer.parseInt(pathInfo.substring("/audit/journey/".length()));
                
                // Buscar a jornada
                Optional<Journey> journeyOpt = journeyDAO.findById(journeyId);
                if (!journeyOpt.isPresent()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Jornada não encontrada")));
                    return;
                }
                
                Journey journey = journeyOpt.get();
                
                // Buscar registros de tempo da jornada
                List<TimeRecord> timeRecords = timeRecordDAO.findByJourneyId(journeyId);
                if (timeRecords.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.serialize(new ErrorResponse("Jornada não possui registros de tempo para auditoria")));
                    return;
                }
                
                // Realizar auditoria
                ComplianceAudit audit = complianceAuditService.performAudit(journey, timeRecords);
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gson.serialize(audit));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.serialize(new ErrorResponse("URL inválida para POST")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada inválido")));
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro ao auditar jornada")));
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
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
        
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // PUT /compliance/{id} - Atualizar auditoria
                int auditId = Integer.parseInt(pathInfo.substring(1));
                
                // Ler o corpo da requisição
                ComplianceAudit audit = gson.deserialize(request.getReader(), ComplianceAudit.class);
                audit.setId(auditId);
                
                boolean updated = complianceAuditService.updateAudit(audit);
                
                if (updated) {
                    out.print(gson.serialize(audit));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Auditoria não encontrada")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.serialize(new ErrorResponse("URL inválida para PUT")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da auditoria inválido")));
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro ao atualizar auditoria")));
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
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
        
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // DELETE /compliance/{id} - Deletar auditoria
                int auditId = Integer.parseInt(pathInfo.substring(1));
                
                boolean deleted = complianceAuditService.deleteAudit(auditId);
                
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Auditoria não encontrada")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.serialize(new ErrorResponse("URL inválida para DELETE")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da auditoria inválido")));
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro ao deletar auditoria")));
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
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