package com.compliancesys.controller;

import java.io.BufferedReader;
import java.io.IOException; // Import adicionado
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; // Import adicionado
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest; // Import adicionado
import javax.servlet.http.HttpServletResponse;

import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;
import com.compliancesys.service.impl.VehicleServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

@WebServlet("/vehicles/*")
public class VehicleServlet extends HttpServlet {

    private VehicleService vehicleService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Instanciando as dependências e passando para o construtor do serviço
        VehicleDAO vehicleDAO = new VehicleDAOImpl();
        Validator validator = new ValidatorImpl();
        this.vehicleService = new VehicleServiceImpl(vehicleDAO, validator);
        this.gsonSerializer = new GsonUtilImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Vehicle> vehicles = vehicleService.getAllVehicles();
                out.print(gsonSerializer.serialize(vehicles));
            } else if (pathInfo.startsWith("/plate/")) {
                String plate = pathInfo.substring("/plate/".length());
                Optional<Vehicle> vehicle = vehicleService.getVehicleByPlate(plate);
                if (vehicle.isPresent()) {
                    out.print(gsonSerializer.serialize(vehicle.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Veículo com placa " + plate + " não encontrado.")));
                }
            } else {
                int vehicleId = Integer.parseInt(pathInfo.substring(1));
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
        } catch (BusinessException e) { // Adicionado BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de negócio: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao processar requisição GET: " + e.getMessage())));
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
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Vehicle vehicle = gsonSerializer.deserialize(sb.toString(), Vehicle.class);

            // A chamada agora retorna um Vehicle
            Vehicle registeredVehicle = vehicleService.registerVehicle(vehicle);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(registeredVehicle));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar veículo: " + e.getMessage())));
        } catch (BusinessException e) { // Adicionado BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de negócio: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar veículo: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do veículo é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int vehicleId = Integer.parseInt(pathInfo.substring(1));

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Vehicle vehicle = gsonSerializer.deserialize(sb.toString(), Vehicle.class);
            vehicle.setId(vehicleId);

            // A chamada agora espera um boolean
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
        } catch (BusinessException e) { // Adicionado BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de negócio: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar veículo: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID do veículo é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int vehicleId = Integer.parseInt(pathInfo.substring(1));
            if (vehicleService.deleteVehicle(vehicleId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
        } catch (BusinessException e) { // Adicionado BusinessException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de negócio: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar veículo: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
