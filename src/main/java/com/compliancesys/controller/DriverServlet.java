package com.compliancesys.controller;

<<<<<<< Updated upstream
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.exception.BusinessException;
=======
import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.exception.BusinessException; // Importa BusinessException
>>>>>>> Stashed changes
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.service.impl.DriverServiceImpl;
import com.compliancesys.util.GsonUtil;
<<<<<<< Updated upstream
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
=======
import com.compliancesys.util.Validator; // Importa Validator
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl; // Importa ValidatorImpl
import jakarta.servlet.ServletException; // Alterado de javax para jakarta
import jakarta.servlet.annotation.WebServlet; // Alterado de javax para jakarta
import jakarta.servlet.http.HttpServlet; // Alterado de javax para jakarta
import jakarta.servlet.http.HttpServletRequest; // Alterado de javax para jakarta
import jakarta.servlet.http.HttpServletResponse; // Alterado de javax para jakarta

import javax.sql.DataSource; // Importa DataSource
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime; // Adicionado para ErrorResponse
import java.util.List;
import java.util.Optional;
import java.util.logging.Level; // Adicionado para Logger
import java.util.logging.Logger; // Adicionado para Logger
import java.util.stream.Collectors; // Adicionado para ler o corpo da requisição

/**
 * Servlet para gerenciar operações CRUD de motoristas.
 * Responde a requisições HTTP para /drivers.
 */
@WebServlet("/drivers/*")
public class DriverServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DriverServlet.class.getName()); // Adicionado Logger
>>>>>>> Stashed changes
    private DriverService driverService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
<<<<<<< Updated upstream
        DriverDAO driverDAO = new DriverDAOImpl();
        Validator validator = new ValidatorImpl(); // Instanciação da implementação concreta
        // CORRIGIDO: Construtor de DriverServiceImpl agora recebe DriverDAO e Validator
        this.driverService = new DriverServiceImpl(driverDAO, validator);
        this.gson = new GsonUtilImpl();
=======
        try {
            DataSource dataSource = DatabaseConfig.getInstance().getDataSource(); // Obtém o DataSource

            // Instanciação da DAO com DataSource
            DriverDAO driverDAO = new DriverDAOImpl(dataSource);

            // Instanciação do utilitário
            Validator validator = new ValidatorImpl();

            // Instanciação do Service com suas dependências
            this.driverService = new DriverServiceImpl(driverDAO, validator);
            this.gsonSerializer = new GsonUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar DriverServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar DriverServlet", e);
        }
>>>>>>> Stashed changes
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

<<<<<<< Updated upstream
        String pathInfo = request.getPathInfo(); // Ex: /1, /cpf/12345678901, /email/test@example.com
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Driver> drivers = driverService.getAllDrivers();
                out.print(gson.serialize(drivers));
            } else if (pathInfo.startsWith("/cpf/")) {
                String cpf = pathInfo.substring("/cpf/".length());
                Optional<Driver> driver = driverService.getDriverByCpf(cpf);
=======
        String pathInfo = request.getPathInfo(); // /drivers/{id}
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /drivers - Retorna todos os motoristas
                List<Driver> drivers = driverService.getAllDrivers();
                out.print(gsonSerializer.serialize(drivers));
            } else {
                // GET /drivers/{id} - Retorna um motorista específico
                int driverId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Driver> driver = driverService.getDriverById(driverId);
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número no GET de motorista: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado no GET de motorista: " + e.getMessage())));
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
            String jsonBody = request.getReader().lines().collect(Collectors.joining()); // Leitura robusta do corpo
            Driver newDriver = gsonSerializer.deserialize(jsonBody, Driver.class);
            Driver createdDriver = driverService.registerDriver(newDriver);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(createdDriver));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar motorista: " + e.getMessage())));
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
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
=======
            int driverId = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining()); // Leitura robusta do corpo
            Driver updatedDriver = gsonSerializer.deserialize(jsonBody, Driver.class);
            updatedDriver.setId(driverId); // Garante que o ID do path seja usado

            boolean updated = driverService.updateDriver(updatedDriver);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(updatedDriver));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Motorista não encontrado para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar motorista: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar motorista: " + e.getMessage())));
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            out.print(gson.serialize(new ErrorResponse("ID do motorista é obrigatório para exclusão.")));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista é obrigatório para exclusão.")));
>>>>>>> Stashed changes
            out.flush();
            return;
        }

        try {
<<<<<<< Updated upstream
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = driverService.deleteDriver(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
=======
            int driverId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = driverService.deleteDriver(driverId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
>>>>>>> Stashed changes
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Motorista não encontrado para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
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
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar motorista: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar motorista: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar motorista: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar motorista: " + e.getMessage())));
>>>>>>> Stashed changes
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
