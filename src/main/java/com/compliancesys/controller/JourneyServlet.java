package com.compliancesys.controller;

import com.compliancesys.model.Journey;
import com.compliancesys.service.JourneyService;
import com.compliancesys.util.GsonSerializer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para gerenciar operações CRUD de jornadas (Journey).
 * Responde a requisições HTTP para /journeys.
 * Inclui API para comunicação JSON (Gson).
 */
@WebServlet("/journeys")
public class JourneyServlet extends HttpServlet {

    private JourneyService journeyService;
    private GsonSerializer gsonSerializer;

    @Override
    public void init() throws ServletException {
        // TODO: Injetar JourneyService e GsonSerializer (ex: via CDI/Spring ou instanciar diretamente para este exemplo)
        // Por simplicidade, instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.journeyService = null; // Substituir pela implementação real
        this.gsonSerializer = null; // Substituir pela implementação real
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /journeys/{id} ou /journeys/driver/{driverId}?date=YYYY-MM-DD

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /journeys - Retorna todas as jornadas
            try {
                List<Journey> journeys = journeyService.getAllJourneys();
                out.print(gsonSerializer.serialize(journeys));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar jornadas: " + e.getMessage())));
            }
        } else if (pathInfo.startsWith("/driver/")) {
            // GET /journeys/driver/{driverId}?date=YYYY-MM-DD
            try {
                int driverId = Integer.parseInt(pathInfo.substring("/driver/".length()));
                String dateParam = request.getParameter("date");
                List<Journey> journeys;

                if (dateParam != null && !dateParam.isEmpty()) {
                    LocalDate journeyDate = LocalDate.parse(dateParam);
                    Optional<Journey> journey = journeyService.getJourneyByDriverIdAndDate(driverId, journeyDate);
                    journeys = journey.map(List::of).orElse(List.of()); // Retorna lista com 1 item ou vazia
                } else {
                    journeys = journeyService.getJourneysByDriverId(driverId);
                }
                out.print(gsonSerializer.serialize(journeys));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
            } catch (java.time.format.DateTimeParseException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar jornadas por motorista: " + e.getMessage())));
            }
        } else {
            // GET /journeys/{id} - Retorna uma jornada específica
            try {
                int journeyId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Journey> journey = journeyService.getJourneyById(journeyId);
                if (journey.isPresent()) {
                    out.print(gsonSerializer.serialize(journey.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada.")));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID de jornada inválido.")));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar jornada: " + e.getMessage())));
            }
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Journey journey = gsonSerializer.deserialize(request.getReader().readLine(), Journey.class);
            int newJourneyId = journeyService.createJourney(journey);
            journey.setId(newJourneyId); // Define o ID gerado no objeto
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(journey));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao criar jornada: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de jornada inválidos: " + e.getMessage())));
        }
        out.flush();
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
            int journeyId = Integer.parseInt(pathInfo.substring(1));
            Journey journey = gsonSerializer.deserialize(request.getReader().readLine(), Journey.class);
            journey.setId(journeyId); // Garante que o ID do path seja usado

            if (journeyService.updateJourney(journey)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(journey));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de jornada inválido.")));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar jornada: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de jornada inválidos: " + e.getMessage())));
        }
        out.flush();
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
            int journeyId = Integer.parseInt(pathInfo.substring(1));
            if (journeyService.deleteJourney(journeyId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Jornada não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de jornada inválido.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar jornada: " + e.getMessage())));
        }
        out.flush();
    }

    // Classe auxiliar para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
