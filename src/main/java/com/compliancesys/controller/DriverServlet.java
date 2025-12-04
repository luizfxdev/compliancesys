package com.compliancesys.controller;

import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.service.impl.DriverServiceImpl; // Assumindo uma implementação
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.impl.GsonUtilImpl; // Assumindo uma implementação
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para gerenciar operações CRUD de motoristas.
 * Responde a requisições HTTP para /drivers.
 */
@WebServlet("/drivers/*") // Adicionado /* para permitir pathInfo
public class DriverServlet extends HttpServlet {

    private DriverService driverService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.driverService = new DriverServiceImpl(); // Você precisará criar DriverServiceImpl
        this.gsonSerializer = new GsonUtilImpl(); // Você precisará criar GsonUtilImpl
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /drivers/{id}
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /drivers - Retorna todos os motoristas
            try {
                List<Driver> drivers = driverService.getAllDrivers();
                out.print(gsonSerializer.serialize(drivers));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar motoristas: " + e.getMessage())));
            }
        } else {
            // GET /drivers/{id} - Retorna um motorista específico
            try {
                int driverId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Driver> driver = driverService.getDriverById(driverId);
                if (driver.isPresent()) {
                    out.print(gsonSerializer.serialize(driver.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Motorista não encontrado.")));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao buscar motorista: " + e.getMessage())));
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
            Driver driver = gsonSerializer.deserialize(request.getReader().readLine(), Driver.class);
            int newDriverId = driverService.registerDriver(driver);
            driver.setId(newDriverId); // Define o ID gerado no objeto
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(driver));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar motorista: " + e.getMessage())));
        } catch (Exception e) { // Captura erros de desserialização ou outros
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de motorista inválidos: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int driverId = Integer.parseInt(pathInfo.substring(1));
            Driver driver = gsonSerializer.deserialize(request.getReader().readLine(), Driver.class);
            driver.setId(driverId); // Garante que o ID do path seja usado

            if (driverService.updateDriver(driver)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(driver));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Motorista não encontrado para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar motorista: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados de motorista inválidos: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do motorista é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int driverId = Integer.parseInt(pathInfo.substring(1));
            if (driverService.deleteDriver(driverId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Motorista não encontrado para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID de motorista inválido.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar motorista: " + e.getMessage())));
        }
        out.flush();
    }

    // Classe auxiliar para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
