package com.compliancesys.controller;

import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.TimeRecordService;
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
 * Servlet para gerenciar operações CRUD de registros de ponto (TimeRecord).
 * Responde a requisições HTTP para /timerecords.
 */
@WebServlet("/timerecords")
public class TimeRecordServlet extends HttpServlet {

    private TimeRecordService timeRecordService;
    private GsonSerializer gsonSerializer;

    @Override
    public void init() throws ServletException {
        // TODO: Injetar TimeRecordService e GsonSerializer (ex: via CDI/Spring ou instanciar diretamente para este exemplo)
        // Por simplicidade, instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.timeRecordService = null; // Substituir pela implementação real
        this.gsonSerializer = null; // Substituir pela implementação real
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /timerecords/{id} ou /timerecords/driver/{driverId}?date=YYYY-MM-DD

        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /timerecords - Retorna todos os registros de ponto
            try {
                List<TimeRecord> timeRecords = timeRecordService.getAllTimeRecords();
                out.print(gsonSerializer.serialize(timeRecords));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar registros de ponto: " + e.getMessage())));
            }
        } else if (pathInfo.startsWith("/driver/")) {
            // GET /timerecords/driver/{driverId}?date=YYYY-MM-DD
            try {
                int driverId = Integer.parseInt(pathInfo.substring("/driver/".length()));
                String dateParam = request.getParameter("date");
                List<TimeRecord> records;

                if (dateParam != null && !dateParam.isEmpty()) {
                    LocalDate date = LocalDate.parse(dateParam);
                    records = timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date);
                } else {
                    records = timeRecordService.getTimeRecordsByDriverId(driverId);
                }
                out.print(gsonSerializer.serialize(records));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
            } catch (java.time.format.DateTimeParseException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar registros de ponto por motorista: " + e.getMessage())));
            }
        } else {
            // GET /timerecords/{id} - Retorna um registro de ponto específico
            try {
                int recordId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<TimeRecord> timeRecord = timeRecordService.getTimeRecordById(recordId);
                if (timeRecord.isPresent()) {
                    out.print(gsonSerializer.serialize(timeRecord.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Registro de ponto não encontrado.")));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar registro de ponto: " + e.getMessage())));
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
            TimeRecord timeRecord = gsonSerializer.deserialize(request.getReader().readLine(), TimeRecord.class);
            int newRecordId = timeRecordService.registerTimeRecord(timeRecord);
            timeRecord.setId(newRecordId); // Define o ID gerado no objeto
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(timeRecord));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar ponto: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de registro de ponto inválidos: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do registro de ponto é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int recordId = Integer.parseInt(pathInfo.substring(1));
            TimeRecord timeRecord = gsonSerializer.deserialize(request.getReader().readLine(), TimeRecord.class);
            timeRecord.setId(recordId); // Garante que o ID do path seja usado

            if (timeRecordService.updateTimeRecord(timeRecord)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(timeRecord));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Registro de ponto não encontrado para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar registro de ponto: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de registro de ponto inválidos: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do registro de ponto é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int recordId = Integer.parseInt(pathInfo.substring(1));
            if (timeRecordService.deleteTimeRecord(recordId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Registro de ponto não encontrado para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar registro de ponto: " + e.getMessage())));
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
