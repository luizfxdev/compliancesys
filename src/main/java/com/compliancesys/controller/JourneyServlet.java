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

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.enums.ComplianceStatus;
import com.compliancesys.service.JourneyService;
import com.compliancesys.service.impl.JourneyServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.TimeUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/journeys/*")
public class JourneyServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(JourneyServlet.class.getName());
    private JourneyService journeyService;
    private GsonUtil gson;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // Obter conexão da instância singleton
            Connection connection = DatabaseConfig.getInstance().getConnection();
            
            JourneyDAOImpl journeyDAO = new JourneyDAOImpl(connection);
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            DriverDAOImpl driverDAO = new DriverDAOImpl(); // Sem parâmetros
            
            this.validator = new ValidatorImpl();
            TimeUtil timeUtil = new TimeUtilImpl();
            this.gson = new GsonUtilImpl();

            this.journeyService = new JourneyServiceImpl(journeyDAO, timeRecordDAO, driverDAO, validator, timeUtil);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar o JourneyServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao conectar ao banco de dados na inicialização do servlet.", e);
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
                List<Journey> journeys = journeyService.getAllJourneys();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.serialize(journeys));
            } else {
                String[] pathParts = pathInfo.substring(1).split("/");
                if (pathParts.length == 1) {
                    int id = Integer.parseInt(pathParts[0]);
                    Optional<Journey> journey = journeyService.getJourneyById(id);
                    if (journey.isPresent()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(gson.serialize(journey.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Jornada não encontrada.")));
                    }
                } else if (pathParts.length == 2 && "driver".equals(pathParts[0])) {
                    int driverId = Integer.parseInt(pathParts[1]);
                    List<Journey> journeys = journeyService.getJourneysByDriverId(driverId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.serialize(journeys));
                } else if (pathParts.length == 3 && "driver".equals(pathParts[0]) && "date".equals(pathParts[1])) {
                    int driverId = Integer.parseInt(pathParts[2]);
                    String dateParam = request.getParameter("date");
                    if (dateParam == null || dateParam.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gson.serialize(new ErrorResponse("Parâmetro 'date' é obrigatório.")));
                        return;
                    }
                    LocalDate journeyDate = LocalDate.parse(dateParam);
                    Optional<Journey> journey = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);
                    if (journey.isPresent()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(gson.serialize(journey.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(
                                new ErrorResponse("Jornada não encontrada para o motorista e data especificados.")));
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Recurso não encontrado ou URL inválida.")));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido.")));
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Formato de data inválido. Use YYYY-MM-DD: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio no doGet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no doGet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno no banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no doGet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado.")));
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

        try {
            Journey newJourney = gson.deserialize(request.getReader(), Journey.class);
            if (newJourney == null || !validator.isValidId(newJourney.getDriverId()) 
                    || newJourney.getJourneyDate() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.serialize(new ErrorResponse("Dados da jornada inválidos.")));
                out.flush();
                return;
            }
            newJourney.setCreatedAt(LocalDateTime.now());
            newJourney.setUpdatedAt(LocalDateTime.now());
            newJourney.setComplianceStatus(ComplianceStatus.PENDING.name());
            newJourney.setTotalDrivingTimeMinutes(0);
            newJourney.setTotalRestTimeMinutes(0);
            newJourney.setDailyLimitExceeded(false);

            Journey createdJourney = journeyService.createJourney(newJourney);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdJourney));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio no doPost: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no doPost: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno no banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no doPost: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado.")));
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
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada é obrigatório.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            Journey updatedJourney = gson.deserialize(request.getReader(), Journey.class);
            updatedJourney.setId(id);
            Journey resultJourney = journeyService.updateJourney(updatedJourney);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.serialize(resultJourney));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado.")));
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
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada é obrigatório.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = journeyService.deleteJourney(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Jornada não encontrada.")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado.")));
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