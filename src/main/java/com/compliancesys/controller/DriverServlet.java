package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.service.impl.DriverServiceImpl; // Importar para o timestamp do ErrorResponse
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl; // Importar para o Logger
import com.compliancesys.util.impl.ValidatorImpl; // Importar para o Logger

@WebServlet("/drivers/*")
public class DriverServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DriverServlet.class.getName()); // Adicionado Logger

    private DriverService driverService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        super.init(); // Chamar o init da superclasse
        try {
            // Assumindo que DriverDAOImpl e ValidatorImpl não lançam exceções checadas no construtor
            DriverDAO driverDAO = new DriverDAOImpl();
            Validator validator = new ValidatorImpl();
            this.driverService = new DriverServiceImpl(driverDAO, validator);
            this.gsonSerializer = new GsonUtilImpl();
            LOGGER.log(Level.INFO, "DriverServlet inicializado com sucesso.");
        } catch (Exception e) { // Captura qualquer exceção durante a inicialização
            LOGGER.log(Level.SEVERE, "Erro ao inicializar DriverServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar DriverServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /drivers - Retorna todos os motoristas
                List<Driver> drivers = driverService.getAllDrivers(); // Método do service pode lançar BusinessException
                out.print(gsonSerializer.serialize(drivers));
            } else {
                // GET /drivers/{id} - Retorna um motorista específico
                // Ajuste para lidar com pathInfo como "/123" ou "/123/"
                String idStr = pathInfo.substring(1); // Remove a primeira barra
                if (idStr.endsWith("/")) {
                    idStr = idStr.substring(0, idStr.length() - 1); // Remove a barra final se houver
                }
                int driverId = Integer.parseInt(idStr); // Pode lançar NumberFormatException
                Optional<Driver> driver = driverService.getDriverById(driverId); // Método do service pode lançar BusinessException
                if (driver.isPresent()) {
                    out.print(gsonSerializer.serialize(driver.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    out.print(gsonSerializer.serialize(new ErrorResponse("Motorista não encontrado com ID: " + driverId)));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de motorista inválido no GET: " + pathInfo + " - " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
        } catch (BusinessException e) { // Captura BusinessException lançada pelo service
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 ou 422 Unprocessable Entity
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro: " + e.getMessage())));
        } catch (Exception e) { // Captura qualquer outra exceção inesperada
            LOGGER.log(Level.SEVERE, "Erro inesperado no doGet do DriverServlet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
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
            // Ler o corpo da requisição. request.getReader().readLine() pode não ser o ideal para JSON complexo.
            // O ideal é usar gsonSerializer.deserialize(request.getReader(), Driver.class);
            // Mas mantendo sua implementação atual para compatibilidade, apenas ajustando o tratamento de exceção.
            String requestBody = request.getReader().lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            Driver driver = gsonSerializer.deserialize(requestBody, Driver.class); // Pode lançar JsonSyntaxException ou BusinessException

            // CORREÇÃO AQUI: Mudando de createDriver para registerDriver
            Driver registeredDriver = driverService.registerDriver(driver); // Método do service pode lançar BusinessException
            response.setStatus(HttpServletResponse.SC_CREATED); // 201
            out.print(gsonSerializer.serialize(registeredDriver));
        } catch (BusinessException e) { // Captura BusinessException lançada pelo service
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 ou 422
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (Exception e) { // Captura JsonSyntaxException ou qualquer outra exceção inesperada
            LOGGER.log(Level.SEVERE, "Erro inesperado no doPost do DriverServlet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 para erros de parsing, 500 para outros
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados inválidos ou erro interno: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (idStr.endsWith("/")) {
                idStr = idStr.substring(0, idStr.length() - 1);
            }
            int driverId = Integer.parseInt(idStr); // Pode lançar NumberFormatException

            String requestBody = request.getReader().lines().collect(java.util.stream.Collectors.joining(System.lineSeparator()));
            Driver driver = gsonSerializer.deserialize(requestBody, Driver.class); // Pode lançar JsonSyntaxException ou BusinessException
            driver.setId(driverId); // Garante que o ID do objeto corresponde ao da URL

            Driver updatedDriver = driverService.updateDriver(driver); // Método do service pode lançar BusinessException
            response.setStatus(HttpServletResponse.SC_OK); // 200
            out.print(gsonSerializer.serialize(updatedDriver));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de motorista inválido no PUT: " + pathInfo + " - " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) { // Captura BusinessException lançada pelo service
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 ou 422
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (Exception e) { // Captura JsonSyntaxException ou qualquer outra exceção inesperada
            LOGGER.log(Level.SEVERE, "Erro inesperado no doPut do DriverServlet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 para erros de parsing, 500 para outros
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados inválidos ou erro interno: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            String idStr = pathInfo.substring(1);
            if (idStr.endsWith("/")) {
                idStr = idStr.substring(0, idStr.length() - 1);
            }
            int driverId = Integer.parseInt(idStr); // Pode lançar NumberFormatException

            if (driverService.deleteDriver(driverId)) { // Método do service pode lançar BusinessException
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                out.print(gsonSerializer.serialize(new ErrorResponse("Motorista não encontrado ou não pôde ser excluído com ID: " + driverId)));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de motorista inválido no DELETE: " + pathInfo + " - " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) { // Captura BusinessException lançada pelo service
            LOGGER.log(Level.WARNING, "Erro de negócio ao excluir motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500, pois se o service lançou, é um erro interno
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro: " + e.getMessage())));
        } catch (Exception e) { // Captura qualquer outra exceção inesperada
            LOGGER.log(Level.SEVERE, "Erro inesperado no doDelete do DriverServlet: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro interno do servidor: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    // Classe auxiliar para padronizar as respostas de erro JSON
    private static class ErrorResponse {
        private String message;
        private String timestamp; // Adicionado timestamp

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = LocalDateTime.now().toString(); // Preenche com o tempo atual
        }

        // Getters para Gson serializar
        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
