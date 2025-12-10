package com.compliancesys.controller;

import com.compliancesys.dao.ComplianceAuditDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.impl.ComplianceAuditDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/journeys/*")
public class JourneyServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(JourneyServlet.class.getName());
    private JourneyService journeyService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
        JourneyDAO journeyDAO = new JourneyDAOImpl();
        TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(); // Adicionado
        ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl(); // Adicionado
        Validator validator = new ValidatorImpl();
        TimeUtil timeUtil = new TimeUtilImpl(); // Adicionado

        // CORRIGIDO: Construtor de JourneyServiceImpl com todos os argumentos esperados
        this.journeyService = new JourneyServiceImpl(journeyDAO, timeRecordDAO, complianceAuditDAO, validator, timeUtil);
        this.gson = new GsonUtilImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // Ex: /1
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Journey> journeys = journeyService.getAllJourneys();
                out.print(gson.serialize(journeys));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Journey> journey = journeyService.getJourneyById(id);
                if (journey.isPresent()) {
                    out.print(gson.serialize(journey.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Jornada não encontrada com o ID: " + id)));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID ou formato de URL inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número no GET de jornada: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de jornada: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Lê o corpo da requisição como String
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Journey newJourney = gson.deserialize(jsonBody, Journey.class);
            Journey createdJourney = journeyService.createJourney(newJourney);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdJourney));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));

            // Lê o corpo da requisição como String
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Journey updatedJourney = gson.deserialize(jsonBody, Journey.class);
            updatedJourney.setId(id);

            Journey result = journeyService.updateJourney(updatedJourney);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.serialize(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar jornada: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar jornada: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor ao atualizar jornada: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar jornada: " + e.getMessage(), e);
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada é obrigatório para exclusão.")));
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
                out.print(gson.serialize(new ErrorResponse("Jornada não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID da jornada inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar jornada: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar jornada: " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor ao deletar jornada: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar jornada: " + e.getMessage(), e);
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
