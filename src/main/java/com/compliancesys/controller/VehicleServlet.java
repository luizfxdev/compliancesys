package com.compliancesys.controller;

import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;
import com.compliancesys.service.impl.VehicleServiceImpl; // Assumindo uma implementação
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
 * Servlet para gerenciar operações CRUD de veículos.
 * Responde a requisições HTTP para /vehicles.
 */
@WebServlet("/vehicles/*") // Adicionado /* para permitir pathInfo
public class VehicleServlet extends HttpServlet {

    private VehicleService vehicleService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.vehicleService = new VehicleServiceImpl(); // Você precisará criar VehicleServiceImpl
        this.gsonSerializer = new GsonUtilImpl(); // Você precisará criar GsonUtilImpl
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /vehicles/{id}

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /vehicles - Retorna todos os veículos
                List<Vehicle> vehicles = vehicleService.getAllVehicles();
                out.print(gsonSerializer.serialize(vehicles));
            } else {
                // GET /vehicles/{id} - Retorna um veículo específico
                int vehicleId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<Vehicle> vehicle = vehicleService.getVehicleById(vehicleId);
                if (vehicle.isPresent()) {
                    out.print(gsonSerializer.serialize(vehicle.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Veículo não encontrado.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado: " + e.getMessage())));
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Vehicle vehicle = gsonSerializer.deserialize(request.getReader().readLine(), Vehicle.class);
            int newVehicleId = vehicleService.registerVehicle(vehicle);
            vehicle.setId(newVehicleId); // Define o ID gerado no objeto
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(vehicle));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar veículo: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados do veículo inválidos: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do veículo é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int vehicleId = Integer.parseInt(pathInfo.substring(1));
            Vehicle vehicle = gsonSerializer.deserialize(request.getReader().readLine(), Vehicle.class);
            vehicle.setId(vehicleId); // Garante que o ID do path seja usado

            if (vehicleService.updateVehicle(vehicle)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(vehicle));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Veículo não encontrado para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do veículo inválido.")));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar veículo: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Dados do veículo inválidos: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do veículo é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int vehicleId = Integer.parseInt(pathInfo.substring(1));
            if (vehicleService.deleteVehicle(vehicleId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Veículo não encontrado para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do veículo inválido.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar veículo: " + e.getMessage())));
        }
        out.flush();
    }

    // Classe auxiliar para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
