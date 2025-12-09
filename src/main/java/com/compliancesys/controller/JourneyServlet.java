package com.compliancesys.controller;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@WebServlet("/journeys/*")
public class JourneyServlet extends HttpServlet {

    private JourneyService journeyService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Em um projeto real, considere usar um framework de injeção de dependência.
        // Instanciações diretas para fins de exemplo.
        JourneyDAO journeyDAO = new JourneyDAOImpl();
        TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl();
        Validator validator = new ValidatorImpl();
        TimeUtil timeUtil = new TimeUtilImpl();
        this.journeyService = new JourneyServiceImpl(journeyDAO, timeRecordDAO, validator, timeUtil);
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
                // GET /journeys - Retorna todas as jornadas
                List<Journey> journeys = journeyService.getAllJourneys();
                out.print(gsonSerializer.serialize(journeys));
            } else if (pathInfo.startsWith("/driver/")) {
                // GET /journeys/driver/{driverId}?date=YYYY-MM-DD
                int driverId = Integer.parseInt(pathInfo.substring("/driver/".length()));
                String dateParam = request.getParameter("date");
                List<Journey> journeys;

                if (dateParam != null && !dateParam.isEmpty()) {
                    LocalDate journeyDate = LocalDate.parse(dateParam);
                    Optional<Journey> journey = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);
                    journeys = journey.map(List::of).orElse(List.of()); // Converte Optional<Journey> para List<Journey>
                } else {
                    journeys = journeyService.getJourneysByDriverId(driverId);
                }
                out.print(gsonSerializer.serialize(journeys));
            } else {
                // GET /journeys/{id} - Retorna uma jornada específica
                int journeyId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Journey> journey = journeyService.getJourneyById(journeyId);
                if (journey.isPresent()) {
                    out.print(gsonSerializer.serialize(journey.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Ou SC_BAD_REQUEST dependendo da natureza da BusinessException
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de negócio: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao processar requisição GET: " + e.getMessage())));
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
            // Lê o corpo completo da requisição
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Journey journey = gsonSerializer.deserialize(sb.toString(), Journey.class);

            Journey createdJourney = journeyService.createJourney(journey);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(createdJourney));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (IllegalArgumentException e) { // Adicionado para capturar validações de entrada
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados inválidos: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Erros inesperados no servidor
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar jornada: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da jornada é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int journeyId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial

            // Lê o corpo completo da requisição
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Journey journey = gsonSerializer.deserialize(sb.toString(), Journey.class);
            journey.setId(journeyId); // Garante que o ID do path seja usado

            Journey updatedJourney = journeyService.updateJourney(journey);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonSerializer.serialize(updatedJourney));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Erro de negócio, geralmente 400
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (IllegalArgumentException e) { // Adicionado para capturar validações de entrada
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados inválidos: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Erros inesperados no servidor
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar jornada: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da jornada é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int journeyId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
            if (journeyService.deleteJourney(journeyId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Ou SC_BAD_REQUEST dependendo da natureza da BusinessException
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de negócio ao deletar jornada: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar jornada: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    // Classe auxiliar para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
