package com.compliancesys.controller;

<<<<<<< Updated upstream
=======
import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.service.impl.TimeRecordServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator; // Adicionado para TimeRecordServiceImpl
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl; // Adicionado para TimeRecordServiceImpl
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
>>>>>>> Stashed changes
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
<<<<<<< Updated upstream
import java.time.LocalDateTime;
=======
import java.time.LocalDate;
import java.time.LocalDateTime; // Adicionado para ErrorResponse
import java.time.format.DateTimeParseException;
>>>>>>> Stashed changes
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

<<<<<<< Updated upstream
import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.service.impl.TimeRecordServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

=======
/**
 * Servlet para gerenciar operações CRUD de registros de ponto (TimeRecord).
 * Responde a requisições HTTP para /timerecords.
 */
>>>>>>> Stashed changes
@WebServlet("/timerecords/*")
public class TimeRecordServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordServlet.class.getName());
    private TimeRecordService timeRecordService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
<<<<<<< Updated upstream
        this.gson = new GsonUtilImpl();
=======
        try {
            DataSource dataSource = DatabaseConfig.getInstance().getDataSource();
            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(dataSource); // Injeta DataSource na DAO
            Validator validator = new ValidatorImpl(); // Instancia Validator
            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, validator); // Injeta DAO e Validator no Service
            this.gsonSerializer = new GsonUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar TimeRecordServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar TimeRecordServlet", e);
        }
>>>>>>> Stashed changes
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
<<<<<<< Updated upstream
        Connection connection = null;

        try {
            connection = DatabaseConfig.getInstance().getConnection(); // CORRIGIDO
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            Validator validator = new ValidatorImpl(); // CORRIGIDO
            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, validator); // CORRIGIDO: sem TimeUtil

            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<TimeRecord> timeRecords = timeRecordService.getAllTimeRecords();
                out.print(gson.serialize(timeRecords));
            } else {
                String idStr = pathInfo.substring(1);
                if (idStr.endsWith("/")) {
                    idStr = idStr.substring(0, idStr.length() - 1);
                }
                int timeRecordId = Integer.parseInt(idStr);
                Optional<TimeRecord> timeRecord = timeRecordService.getTimeRecordById(timeRecordId);
                if (timeRecord.isPresent()) {
                    out.print(gson.serialize(timeRecord.get()));
=======

        String pathInfo = request.getPathInfo();
        String driverIdParam = request.getParameter("driverId"); // Parâmetro para buscar por motorista
        String dateParam = request.getParameter("date"); // Parâmetro para buscar por data

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                if (driverIdParam != null && !driverIdParam.isEmpty() && dateParam != null && !dateParam.isEmpty()) {
                    // GET /timerecords?driverId={id}&date={yyyy-MM-dd} - Busca por motorista e data
                    int driverId = Integer.parseInt(driverIdParam);
                    LocalDate date = LocalDate.parse(dateParam);
                    List<TimeRecord> records = timeRecordService.getTimeRecordsByDriverAndDate(driverId, date);
                    out.print(gsonSerializer.serialize(records));
                } else if (driverIdParam != null && !driverIdParam.isEmpty()) {
                    // GET /timerecords?driverId={id} - Busca todos os registros de um motorista
                    int driverId = Integer.parseInt(driverIdParam);
                    List<TimeRecord> records = timeRecordService.getTimeRecordsByDriver(driverId);
                    out.print(gsonSerializer.serialize(records));
                } else {
                    // GET /timerecords - Retorna todos os registros (se aplicável, ou erro se muitos)
                    // Para evitar retornar muitos dados, pode-se exigir parâmetros ou implementar paginação.
                    // Por enquanto, vamos retornar todos, mas com um aviso.
                    LOGGER.log(Level.INFO, "Requisição GET /timerecords sem parâmetros. Retornando todos os registros.");
                    List<TimeRecord> records = timeRecordService.getAllTimeRecords();
                    out.print(gsonSerializer.serialize(records));
                }
            } else {
                // GET /timerecords/{id} - Retorna um registro específico
                int recordId = Integer.parseInt(pathInfo.substring(1));
                Optional<TimeRecord> record = timeRecordService.getTimeRecordById(recordId);
                if (record.isPresent()) {
                    out.print(gsonSerializer.serialize(record.get()));
>>>>>>> Stashed changes
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Registro de ponto não encontrado.")));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID ou driverId inválido no caminho/parâmetro da URL.")));
            LOGGER.log(Level.WARNING, "ID ou driverId inválido no GET de registro de ponto: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Formato de data inválido. Use yyyy-MM-dd.")));
            LOGGER.log(Level.WARNING, "Formato de data inválido no GET de registro de ponto: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de registro de ponto: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de registro de ponto: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor.")));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
                }
            }
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado no GET de registro de ponto: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de registro de ponto: " + e.getMessage(), e);
        } finally {
>>>>>>> Stashed changes
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Connection connection = null;

        try {
<<<<<<< Updated upstream
            connection = DatabaseConfig.getInstance().getConnection(); // CORRIGIDO
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            Validator validator = new ValidatorImpl(); // CORRIGIDO
            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, validator); // CORRIGIDO

            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            TimeRecord newTimeRecord = gson.deserialize(jsonBody, TimeRecord.class);
            TimeRecord createdTimeRecord = timeRecordService.createTimeRecord(newTimeRecord);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdTimeRecord));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            TimeRecord newRecord = gsonSerializer.deserialize(jsonBody, TimeRecord.class);
            TimeRecord createdRecord = timeRecordService.registerTimeRecord(newRecord);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(createdRecord));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar registro de ponto: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor.")));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
                }
            }
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de ponto: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar registro de ponto: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar registro de ponto: " + e.getMessage(), e);
        } finally {
>>>>>>> Stashed changes
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Connection connection = null;

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
<<<<<<< Updated upstream
            connection = DatabaseConfig.getInstance().getConnection(); // CORRIGIDO
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            Validator validator = new ValidatorImpl(); // CORRIGIDO
            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, validator); // CORRIGIDO

            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            TimeRecord updatedTimeRecord = gson.deserialize(jsonBody, TimeRecord.class);
            updatedTimeRecord.setId(id);

            boolean updated = timeRecordService.updateTimeRecord(updatedTimeRecord);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.serialize(updatedTimeRecord));
=======
            int recordId = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            TimeRecord updatedRecord = gsonSerializer.deserialize(jsonBody, TimeRecord.class);
            updatedRecord.setId(recordId); // Garante que o ID do path seja usado

            boolean updated = timeRecordService.updateTimeRecord(updatedRecord);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(updatedRecord));
>>>>>>> Stashed changes
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Registro não encontrado.")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar registro de ponto: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar registro de ponto: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor.")));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
                }
            }
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar registro de ponto: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar registro de ponto: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar registro de ponto: " + e.getMessage(), e);
        } finally {
>>>>>>> Stashed changes
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Connection connection = null;

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
<<<<<<< Updated upstream
            connection = DatabaseConfig.getInstance().getConnection(); // CORRIGIDO
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            Validator validator = new ValidatorImpl(); // CORRIGIDO
            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, validator); // CORRIGIDO

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = timeRecordService.deleteTimeRecord(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
=======
            int recordId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = timeRecordService.deleteTimeRecord(recordId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
>>>>>>> Stashed changes
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Registro não encontrado.")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar registro de ponto: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar registro de ponto: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor.")));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
                }
            }
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar registro de ponto: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar registro de ponto: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar registro de ponto: " + e.getMessage(), e);
        } finally {
>>>>>>> Stashed changes
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