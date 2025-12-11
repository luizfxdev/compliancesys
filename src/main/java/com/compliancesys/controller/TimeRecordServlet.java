package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

@WebServlet("/timerecords/*")
public class TimeRecordServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(TimeRecordServlet.class.getName());
    private TimeRecordService timeRecordService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
        this.gson = new GsonUtilImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
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
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Registro de ponto não encontrado.")));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID de registro de ponto inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET: " + e.getMessage(), e);
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Registro não encontrado.")));
            }
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
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor.")));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
                }
            }
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
            connection = DatabaseConfig.getInstance().getConnection(); // CORRIGIDO
            TimeRecordDAOImpl timeRecordDAO = new TimeRecordDAOImpl(connection);
            Validator validator = new ValidatorImpl(); // CORRIGIDO
            this.timeRecordService = new TimeRecordServiceImpl(timeRecordDAO, validator); // CORRIGIDO

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = timeRecordService.deleteTimeRecord(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Registro não encontrado.")));
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
            out.print(gson.serialize(new ErrorResponse("Erro interno do servidor.")));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao fechar conexão: " + e.getMessage(), e);
                }
            }
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