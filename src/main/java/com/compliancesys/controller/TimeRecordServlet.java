package com.compliancesys.controller;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.model.enums.EventType;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.service.impl.TimeRecordServiceImpl;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/timerecords/*")
public class TimeRecordServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordServlet.class.getName());
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
            LOGGER.log(Level.SEVERE, "Erro ao inicializar TimeRecordServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar TimeRecordServlet", e);
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
            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            TimeRecordService timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, driverDAO, journeyDAO, validator);

            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<TimeRecord> timeRecords = timeRecordService.getAllTimeRecords();
                out.print(gsonUtil.serialize(timeRecords));
            } else if (pathInfo.matches("/\\d+")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<TimeRecord> timeRecord = timeRecordService.getTimeRecordById(id);
                if (timeRecord.isPresent()) {
                    out.print(gsonUtil.serialize(timeRecord.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonUtil.serialize(new ErrorResponse("Registro de tempo não encontrado")));
                }
            } else if (pathInfo.startsWith("/journey/")) {
                int journeyId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
                List<TimeRecord> timeRecords = timeRecordService.getTimeRecordsByJourneyId(journeyId);
                out.print(gsonUtil.serialize(timeRecords));
            } else if (pathInfo.startsWith("/driver/")) {
                int driverId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
                List<TimeRecord> timeRecords = timeRecordService.getTimeRecordsByDriverId(driverId);
                out.print(gsonUtil.serialize(timeRecords));
            } else if (pathInfo.startsWith("/eventtype/")) {
                String eventTypeString = pathInfo.substring(pathInfo.lastIndexOf('/') + 1).toUpperCase();
                try {
                    EventType eventType = EventType.valueOf(eventTypeString);
                    List<TimeRecord> timeRecords = timeRecordService.getTimeRecordsByEventType(eventType);
                    out.print(gsonUtil.serialize(timeRecords));
                } catch (IllegalArgumentException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonUtil.serialize(new ErrorResponse("Tipo de evento inválido: " + eventTypeString)));
                    LOGGER.log(Level.WARNING, "Tipo de evento inválido: " + e.getMessage(), e);
                }
            } else if (pathInfo.equals("/range")) {
                String startParam = request.getParameter("start");
                String endParam = request.getParameter("end");
                if (startParam != null && endParam != null) {
                    LocalDateTime start = LocalDateTime.parse(startParam);
                    LocalDateTime end = LocalDateTime.parse(endParam);
                    List<TimeRecord> timeRecords = timeRecordService.getTimeRecordsByRecordTimeRange(start, end);
                    out.print(gsonUtil.serialize(timeRecords));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonUtil.serialize(new ErrorResponse("Parâmetros 'start' e 'end' são obrigatórios para busca por intervalo de tempo.")));
                }
            } else if (pathInfo.equals("/latest")) {
                String driverIdParam = request.getParameter("driverId");
                String journeyIdParam = request.getParameter("journeyId");
                if (driverIdParam != null && journeyIdParam != null) {
                    int driverId = Integer.parseInt(driverIdParam);
                    int journeyId = Integer.parseInt(journeyIdParam);
                    Optional<TimeRecord> latestRecord = timeRecordService.getLatestTimeRecordByDriverAndJourney(driverId, journeyId);
                    if (latestRecord.isPresent()) {
                        out.print(gsonUtil.serialize(latestRecord.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gsonUtil.serialize(new ErrorResponse("Nenhum registro de tempo encontrado para o motorista e jornada especificados.")));
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonUtil.serialize(new ErrorResponse("Parâmetros 'driverId' e 'journeyId' são obrigatórios para buscar o último registro.")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonUtil.serialize(new ErrorResponse("URL inválida")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID ou parâmetro numérico inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID ou parâmetro numérico inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
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
            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            TimeRecordService timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, driverDAO, journeyDAO, validator);

            TimeRecord timeRecord = gsonUtil.deserialize(request.getReader(), TimeRecord.class);
            TimeRecord createdRecord = timeRecordService.createTimeRecord(timeRecord);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonUtil.serialize(createdRecord));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar registro de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar registro de tempo: " + e.getMessage(), e);
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

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID do registro de tempo é obrigatório para atualização")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            TimeRecordService timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, driverDAO, journeyDAO, validator);

            int id = Integer.parseInt(pathInfo.substring(1));
            TimeRecord updatedRecord = gsonUtil.deserialize(request.getReader(), TimeRecord.class);
            updatedRecord.setId(id);

            TimeRecord result = timeRecordService.updateTimeRecord(updatedRecord);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonUtil.serialize(result));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
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

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID é obrigatório para exclusão")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            TimeRecordService timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, driverDAO, journeyDAO, validator);

            int recordId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = timeRecordService.deleteTimeRecord(recordId);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Registro não encontrado")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
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