package com.compliancesys.controller;

<<<<<<< Updated upstream
=======
import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.DriverDAO; // Adicionado para ComplianceServiceImpl
import com.compliancesys.dao.JourneyDAO; // Adicionado para ComplianceServiceImpl
import com.compliancesys.dao.TimeRecordDAO; // Adicionado para ComplianceServiceImpl
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl; // Adicionado para ComplianceServiceImpl
import com.compliancesys.dao.impl.JourneyDAOImpl; // Adicionado para ComplianceServiceImpl
import com.compliancesys.dao.impl.TimeRecordDAOImpl; // Adicionado para ComplianceServiceImpl
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.ComplianceAudit;
import com.compliancesys.model.report.ComplianceReport; // Adicionado para relatórios
import com.compliancesys.service.ComplianceService;
import com.compliancesys.service.impl.ComplianceServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.TimeUtil; // Adicionado para ComplianceServiceImpl
import com.compliancesys.util.Validator; // Adicionado para ComplianceServiceImpl
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.TimeUtilImpl; // Adicionado para ComplianceServiceImpl
import com.compliancesys.util.impl.ValidatorImpl; // Adicionado para ComplianceServiceImpl
import jakarta.servlet.ServletException; // Alterado de javax para jakarta
import jakarta.servlet.annotation.WebServlet; // Alterado de javax para jakarta
import jakarta.servlet.http.HttpServlet; // Alterado de javax para jakarta
import jakarta.servlet.http.HttpServletRequest; // Alterado de javax para jakarta
import jakarta.servlet.http.HttpServletResponse; // Alterado de javax para jakarta

import javax.sql.DataSource; // Importa DataSource
>>>>>>> Stashed changes
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
<<<<<<< Updated upstream
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
=======
import java.time.LocalDateTime; // Adicionado para ErrorResponse
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level; // Adicionado para Logger
import java.util.logging.Logger; // Adicionado para Logger
import java.util.stream.Collectors; // Adicionado para ler o corpo da requisição

/**
 * Servlet para gerenciar operações de auditoria e relatórios de conformidade.
 * Responde a requisições HTTP para /compliance.
 */
@WebServlet("/compliance/*")
public class ComplianceServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ComplianceServlet.class.getName()); // Adicionado Logger
>>>>>>> Stashed changes
    private ComplianceService complianceService;
    private JourneyService journeyService; // Adicionado para uso no init
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
<<<<<<< Updated upstream
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
=======
        try {
            DataSource dataSource = DatabaseConfig.getInstance().getDataSource(); // Obtém o DataSource

            // Instanciação das DAOs
            ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl(dataSource);
            JourneyDAO journeyDAO = new JourneyDAOImpl(dataSource);
            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(dataSource);
            DriverDAO driverDAO = new DriverDAOImpl(dataSource);

            // Instanciação dos utilitários
            Validator validator = new ValidatorImpl();
            TimeUtil timeUtil = new TimeUtilImpl(); // Assumindo que você tem uma TimeUtilImpl

            // Injeta todas as dependências no ComplianceServiceImpl
            this.complianceService = new ComplianceServiceImpl(
                    complianceAuditDAO, journeyDAO, timeRecordDAO, driverDAO, validator, timeUtil);
            this.gsonSerializer = new GsonUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar ComplianceServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar ComplianceServlet", e);
>>>>>>> Stashed changes
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

<<<<<<< Updated upstream
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
=======
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
                // GET /compliance/report/driver/{driverId}?start=...&end=... - Gera relatório de conformidade
                String driverIdStr = pathInfo.substring("/report/driver/".length());
                int driverId = Integer.parseInt(driverIdStr);

                String startDateParam = request.getParameter("start");
                String endDateParam = request.getParameter("end");

                if (startDateParam == null || endDateParam == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Parâmetros 'start' e 'end' são obrigatórios para o relatório.")));
                    return;
                }

                LocalDate startDate = LocalDate.parse(startDateParam);
                LocalDate endDate = LocalDate.parse(endDateParam);

                ComplianceReport report = complianceService.generateComplianceReport(driverId, startDate, endDate);
                out.print(gsonSerializer.serialize(report));
            } else {
                // GET /compliance/{auditId} - Retorna uma auditoria específica
                int auditId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<ComplianceAudit> audit = complianceService.getComplianceAuditById(auditId);
                if (audit.isPresent()) {
                    out.print(gsonSerializer.serialize(audit.get()));
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID ou parâmetro numérico inválido no caminho da URL.")));
            LOGGER.log(Level.WARNING, "ID ou parâmetro numérico inválido no GET de conformidade: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
            LOGGER.log(Level.WARNING, "Formato de data inválido no GET de conformidade: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de conformidade: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de conformidade: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado no GET de conformidade: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de conformidade: " + e.getMessage(), e);
        } finally {
            out.flush();
>>>>>>> Stashed changes
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
<<<<<<< Updated upstream
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
=======
        try {
            if (pathInfo != null && pathInfo.startsWith("/audit/journey/")) {
                // POST /compliance/audit/journey/{journeyId} - Inicia uma auditoria para uma jornada
                int journeyId = Integer.parseInt(pathInfo.substring("/audit/journey/".length()));
                ComplianceAudit audit = complianceService.auditJourney(journeyId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(gsonSerializer.serialize(audit));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("Recurso não encontrado ou URL inválida para POST.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da jornada inválido.")));
            LOGGER.log(Level.WARNING, "ID da jornada inválido no POST de auditoria: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no POST de auditoria: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados ao auditar jornada: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no POST de auditoria: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado no POST de auditoria: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no POST de auditoria: " + e.getMessage(), e);
        } finally {
            out.flush();
>>>>>>> Stashed changes
        }
    }

<<<<<<< Updated upstream
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
=======
    // Não há métodos doPut ou doDelete para ComplianceAudit, pois a auditoria é um registro de evento.

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
>>>>>>> Stashed changes
    }
}
