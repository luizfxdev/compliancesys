package com.compliancesys.controller;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.service.impl.DriverServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/drivers/*")
public class DriverServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DriverServlet.class.getName());
    private DriverService driverService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
        DriverDAO driverDAO = new DriverDAOImpl();
        Validator validator = new ValidatorImpl(); // Instanciação da implementação concreta
        // CORRIGIDO: Construtor de DriverServiceImpl agora recebe DriverDAO e Validator
        this.driverService = new DriverServiceImpl(driverDAO, validator);
        this.gson = new GsonUtilImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // Ex: /1, /cpf/12345678901, /email/test@example.com
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Driver> drivers = driverService.getAllDrivers();
                out.print(gson.serialize(drivers));
            } else if (pathInfo.startsWith("/cpf/")) {
                String cpf = pathInfo.substring("/cpf/".length());
                Optional<Driver> driver = driverService.getDriverByCpf(cpf);
                if (driver.isPresent()) {
                    out.print(gson.serialize(driver.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Motorista não encontrado com o CPF: " + cpf)));
                }
            } else if (pathInfo.startsWith("/email/")) {
                String email = pathInfo.substring("/email/".length());
                Optional<Driver> driver = driverService.getDriverByEmail(email);
                if (driver.isPresent()) {
                    out.print(gson.serialize(driver.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Motorista não encontrado com o email: " + email)));
                }
            } else {
                int id = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Driver> driver = driverService.getDriverById(id);
                if (driver.isPresent()) {
                    out.print(gson.serialize(driver.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Motorista não encontrado com o ID: " + id)));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID ou formato de URL inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número no GET de motorista: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado no GET de motorista: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de motorista: " + e.getMessage(), e);
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
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Driver newDriver = gson.deserialize(jsonBody, Driver.class);
            // CORRIGIDO: Chamada de método para createDriver
            Driver createdDriver = driverService.createDriver(newDriver);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdDriver));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao criar motorista: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar motorista: " + e.getMessage(), e);
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
            out.print(gson.serialize(new ErrorResponse("ID do motorista é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Driver updatedDriver = gson.deserialize(jsonBody, Driver.class);
            updatedDriver.setId(id);

            Driver result = driverService.updateDriver(updatedDriver);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.serialize(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID do motorista inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar motorista: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao atualizar motorista: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar motorista: " + e.getMessage(), e);
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
            out.print(gson.serialize(new ErrorResponse("ID do motorista é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = driverService.deleteDriver(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Motorista não encontrado para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID do motorista inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar motorista: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao deletar motorista: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar motorista: " + e.getMessage(), e);
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
