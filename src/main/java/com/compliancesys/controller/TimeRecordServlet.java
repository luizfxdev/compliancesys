// src/main/java/com/compliancesys/controller/TimeRecordServlet.java
package com.compliancesys.controller;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.TimeRecordDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;
import com.compliancesys.service.TimeRecordService;
import com.compliancesys.service.impl.TimeRecordServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.TimeUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;
import com.google.gson.Gson; // Importar Gson para o ErrorResponse

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/timerecords/*")
public class TimeRecordServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TimeRecordServlet.class.getName());
    private TimeRecordService timeRecordService;
    private GsonUtil gson;
    private Connection connection; // Adicionar campo para a conexão

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("org.h2.Driver"); // Ou o driver do seu banco de dados
            this.connection = DriverManager.getConnection("jdbc:h2:mem:compliancesys;DB_CLOSE_DELAY=-1", "sa", ""); // Ajuste conforme seu banco

            TimeRecordDAO timeRecordDAO = new TimeRecordDAOImpl(connection);
            JourneyDAO journeyDAO = new JourneyDAOImpl(connection); // Necessário para TimeRecordServiceImpl
            DriverDAO driverDAO = new DriverDAOImpl(connection); // Necessário para TimeRecordServiceImpl
            Validator validator = new ValidatorImpl();
            TimeUtil timeUtil = new TimeUtilImpl();

            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, journeyDAO, driverDAO, validator, timeUtil);
            this.gson = new GsonUtilImpl();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar TimeRecordServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar TimeRecordServlet", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao fechar conexão do banco de dados no TimeRecordServlet: " + e.getMessage(), e);
            }
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
                // Buscar todos os registros de tempo
                List<TimeRecord> timeRecords = timeRecordService.getAllTimeRecords();
                out.print(gson.serialize(timeRecords));
            } else {
                // Lógica para buscar por ID, driverId, driverId e date, journeyId
                String[] pathSegments = pathInfo.substring(1).split("/"); // Remove a barra inicial e divide
                if (pathSegments.length == 1 && pathSegments[0].matches("\\d+")) {
                    // Buscar por ID: /api/timerecords/{id}
                    int id = Integer.parseInt(pathSegments[0]);
                    Optional<TimeRecord> recordOptional = timeRecordService.getTimeRecordById(id);
                    if (recordOptional.isPresent()) {
                        out.print(gson.serialize(recordOptional.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Registro de tempo não encontrado")));
                    }
                } else if (pathSegments.length == 2 && pathSegments[0].equals("driver") && pathSegments[1].matches("\\d+")) {
                    // Buscar por driverId: /api/timerecords/driver/{driverId}
                    int driverId = Integer.parseInt(pathSegments[1]);
                    List<TimeRecord> records = timeRecordService.getTimeRecordsByDriverId(driverId); // Chamada corrigida
                    out.print(gson.serialize(records));
                } else if (pathSegments.length == 4 && pathSegments[0].equals("driver") && pathSegments[2].equals("date") &&
                           pathSegments[1].matches("\\d+")) {
                    // Buscar por driverId e date: /api/timerecords/driver/{driverId}/date/{yyyy-MM-dd}
                    int driverId = Integer.parseInt(pathSegments[1]);
                    LocalDate date = LocalDate.parse(pathSegments[3]);
                    Optional<TimeRecord> recordOptional = timeRecordService.getTimeRecordsByDriverIdAndDate(driverId, date); // Chamada corrigida
                    if (recordOptional.isPresent()) {
                        out.print(gson.serialize(recordOptional.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.serialize(new ErrorResponse("Registro de tempo não encontrado para o motorista e data especificados")));
                    }
                } else if (pathSegments.length == 2 && pathSegments[0].equals("journey") && pathSegments[1].matches("\\d+")) {
                    // Buscar por journeyId: /api/timerecords/journey/{journeyId}
                    int journeyId = Integer.parseInt(pathSegments[1]);
                    List<TimeRecord> records = timeRecordService.getTimeRecordsByJourneyId(journeyId);
                    out.print(gson.serialize(records));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.serialize(new ErrorResponse("Formato de URL inválido para busca de registros de tempo")));
                }
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "ID ou data inválida ao buscar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID ou formato de data inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao buscar registros de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar registros de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
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
            TimeRecord newRecord = gson.deserialize(request.getReader(), TimeRecord.class);
            TimeRecord createdRecord = timeRecordService.createTimeRecord(newRecord); // Chamada corrigida
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdRecord));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar registro de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar registro de tempo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
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
            out.print(gson.serialize(new ErrorResponse("ID do registro de tempo é obrigatório para atualização")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            TimeRecord updatedRecord = gson.deserialize(request.getReader(), TimeRecord.class);
            updatedRecord.setId(id); // Garante que o ID do objeto corresponde ao ID da URL

            TimeRecord result = timeRecordService.updateTimeRecord(updatedRecord);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.serialize(result));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
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
            out.print(gson.serialize(new ErrorResponse("ID é obrigatório para exclusão")));
            out.flush();
            return;
        }

        try {
            int recordId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = timeRecordService.deleteTimeRecord(recordId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Registro não encontrado")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
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
