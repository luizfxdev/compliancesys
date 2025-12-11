package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.Journey;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.report.ComplianceReport; // Importação correta
import com.compliancesys.service.ComplianceService;
import com.compliancesys.service.JourneyService;
import com.compliancesys.service.impl.ComplianceServiceImpl;
import com.compliancesys.service.impl.JourneyServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.TimeUtilImpl; // CORRIGIDO: Pacote de TimeUtilImpl
import com.compliancesys.util.impl.ValidatorImpl; // CORRIGIDO: Pacote de ValidatorImpl

@WebServlet("/api/compliance/*")
public class ComplianceServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServlet.class.getName());
    private ComplianceService complianceService;
    private JourneyService journeyService; // Adicionado para uso no init
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Connection connection = DatabaseConfig.getConnection();
            ComplianceAuditDAOImpl complianceAuditDAO = new ComplianceAuditDAOImpl(connection);
            JourneyDAOImpl journeyDAO = new JourneyDAOImpl(connection);
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            DriverDAOImpl driverDAO = new DriverDAOImpl(connection);
            Validator validator = new ValidatorImpl(); // Instanciação correta
            TimeUtil timeUtil = new TimeUtilImpl(); // Instanciação correta
            this.gsonSerializer = new GsonUtilImpl(); // Instanciação correta

            // Ordem dos argumentos corrigida para o construtor de ComplianceServiceImpl
            this.complianceService = new ComplianceServiceImpl(complianceAuditDAO, journeyDAO, timeRecordDAO, driverDAO, validator, timeUtil);
            // Construtor de JourneyServiceImpl também corrigido
            this.journeyService = new JourneyServiceImpl(journeyDAO, timeRecordDAO, driverDAO, validator, timeUtil);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar o ComplianceServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao conectar ao banco de dados na inicialização do servlet.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // Lógica para listar todos os relatórios ou um resumo
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Parâmetros de consulta ausentes ou inválidos.")));
            out.flush();
            return;
        }

        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length == 3 && "report".equals(pathParts[0])) {
            try {
                int driverId = Integer.parseInt(pathParts[1]);
                LocalDate journeyDate = LocalDate.parse(pathParts[2]); // Assume que a data está no formato YYYY-MM-DD

                // Aqui você pode chamar um método para gerar um relatório para um motorista e data específica
                // Por exemplo, buscar uma jornada e suas auditorias para aquele dia
                Optional<Journey> journey = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);

                if (journey.isPresent()) {
                    // Se a jornada existe, podemos gerar um relatório mais detalhado ou apenas retornar a jornada
                    // Por simplicidade, vamos retornar a jornada e suas auditorias se existirem
                    List<ComplianceAudit> audits = complianceService.getAuditsByDriverIdAndDateRange(driverId, journeyDate, journeyDate);
                    // Você pode criar um objeto de resposta mais complexo aqui
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gsonSerializer.serialize(journey.get())); // Retorna a jornada encontrada
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada para o motorista e data especificados.")));
                }

            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "ID do motorista inválido: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista inválido.")));
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Formato de data inválido. Use YYYY-MM-DD: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
            } catch (BusinessException e) {
                LOGGER.log(Level.WARNING, "Erro de negócio no doGet: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro de SQL no doGet: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro interno no banco de dados.")));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro inesperado no doGet: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado.")));
            }
        } else if (pathParts.length == 4 && "report".equals(pathParts[0]) && "driver".equals(pathParts[1])) {
            try {
                int driverId = Integer.parseInt(pathParts[2]);
                String dateRange = pathParts[3]; // Ex: "2023-01-01_2023-01-31"
                String[] dates = dateRange.split("_");
                if (dates.length != 2) {
                    throw new BusinessException("Formato de período de datas inválido. Use YYYY-MM-DD_YYYY-MM-DD.");
                }
                LocalDate startDate = LocalDate.parse(dates[0]);
                LocalDate endDate = LocalDate.parse(dates[1]);

                ComplianceReport report = complianceService.generateDriverComplianceReport(driverId, startDate, endDate);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(report));

            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "ID do motorista inválido: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista inválido.")));
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Formato de data inválido. Use YYYY-MM-DD: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
            } catch (BusinessException e) {
                LOGGER.log(Level.WARNING, "Erro de negócio no doGet: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro de SQL no doGet: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro interno no banco de dados.")));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro inesperado no doGet: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado.")));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gsonSerializer.serialize(new ErrorResponse("Recurso não encontrado ou URL inválida.")));
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/audit")) {
            try {
                AuditRequest auditRequest = gsonSerializer.deserialize(request.getReader(), AuditRequest.class);
                if (auditRequest == null || !validator.isValidId(auditRequest.getDriverId()) || auditRequest.getJourneyDate() == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Dados de auditoria inválidos.")));
                    out.flush();
                    return;
                }

                // Primeiro, buscar a jornada para a data e motorista
                Optional<Journey> optionalJourney = journeyService.getJourneyByDriverIdAndDate(auditRequest.getDriverId(), auditRequest.getJourneyDate());
                if (!optionalJourney.isPresent()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada para o motorista e data especificados.")));
                    out.flush();
                    return;
                }

                ComplianceAudit audit = complianceService.auditJourney(optionalJourney.get().getId());
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gsonSerializer.serialize(audit));

            } catch (BusinessException e) {
                LOGGER.log(Level.WARNING, "Erro de negócio no doPost: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro de SQL no doPost: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro interno no banco de dados.")));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro inesperado no doPost: " + e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado.")));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gsonSerializer.serialize(new ErrorResponse("Recurso não encontrado ou URL inválida para POST.")));
        }
        out.flush();
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

    private static class SuccessResponse {
        private String message;
        public SuccessResponse(String message) { this.message = message; }
    }

    private static class AuditRequest {
        private int driverId;
        private LocalDate journeyDate;

        public AuditRequest() {}

        public int getDriverId() { return driverId; }
        public void setDriverId(int driverId) { this.driverId = driverId; }
        public LocalDate getJourneyDate() { return journeyDate; }
        public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    }
}
