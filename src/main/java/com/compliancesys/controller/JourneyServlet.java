package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
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
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.TimeUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/journeys/*")
public class JourneyServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(JourneyServlet.class.getName());
    private JourneyService journeyService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        // Inicialização das dependências
        JourneyDAO journeyDAO = new JourneyDAOImpl();
        TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl();
        ComplianceAuditDAO complianceAuditDAO = new ComplianceAuditDAOImpl();

        // Use as implementações concretas
        Validator validator = new ValidatorImpl();
        TimeUtil timeUtil = new TimeUtilImpl();

        // Passando todas as dependências para o construtor do JourneyServiceImpl
        this.journeyService = new JourneyServiceImpl(journeyDAO, timeRecordDAO, complianceAuditDAO, validator, timeUtil);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /journeys/{id} ou /journeys/driver/{driverId}

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /journeys - Retorna todas as jornadas
                List<Journey> journeys = journeyService.getAllJourneys();
                out.print(gson.toJson(journeys));
            } else if (pathInfo.startsWith("/driver/")) {
                // GET /journeys/driver/{driverId}?date={journeyDate}
                String driverIdStr = pathInfo.substring("/driver/".length());
                int driverId = Integer.parseInt(driverIdStr);
                String dateParam = request.getParameter("date");

                if (dateParam != null && !dateParam.isEmpty()) {
                    // Busca por driverId e data específica
                    LocalDate journeyDate = LocalDate.parse(dateParam);
                    Optional<Journey> journey = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);
                    if (journey.isPresent()) {
                        out.print(gson.toJson(journey.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.toJson(new ErrorResponse("Jornada não encontrada para o motorista e data especificados.")));
                    }
                } else {
                    // Busca todas as jornadas de um motorista
                    List<Journey> journeys = journeyService.getJourneysByDriverId(driverId);
                    out.print(gson.toJson(journeys));
                }
            } else {
                // GET /journeys/{id}
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<Journey> journey = journeyService.getJourneyById(id);
                if (journey.isPresent()) {
                    out.print(gson.toJson(journey.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ErrorResponse("Jornada com ID " + id + " não encontrada.")));
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse("ID ou parâmetros de data inválidos.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ou data: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
            LOGGER.log(Level.WARNING, "Erro de parse de data: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao processar requisição GET: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao processar requisição GET: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Journey newJourney = gson.fromJson(request.getReader(), Journey.class);
            Journey createdJourney = journeyService.createJourney(newJourney);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(createdJourney));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar jornada: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Erro interno do servidor ao criar jornada: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar jornada: " + e.getMessage(), e);
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
            out.print(gson.toJson(new ErrorResponse("ID da jornada é obrigatório para atualização.")));
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            Journey updatedJourney = gson.fromJson(request.getReader(), Journey.class);
            updatedJourney.setId(id); // Garante que o ID do path seja usado

            Journey result = journeyService.updateJourney(updatedJourney);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse("ID da jornada inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar jornada: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar jornada: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Erro interno do servidor ao atualizar jornada: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar jornada: " + e.getMessage(), e);
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
            out.print(gson.toJson(new ErrorResponse("ID da jornada é obrigatório para exclusão.")));
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = journeyService.deleteJourney(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ErrorResponse("Jornada não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse("ID da jornada inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar jornada: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar jornada: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Erro interno do servidor ao deletar jornada: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar jornada: " + e.getMessage(), e);
        }
    }

    // Classe auxiliar para respostas de erro
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}