package com.compliancesys.controller;

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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/vehicles/*")
public class VehicleServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VehicleServlet.class.getName());
    private VehicleService vehicleService;
    private GsonUtil gson;

    @Override
    public void init() throws ServletException {
        VehicleDAO vehicleDAO = new VehicleDAOImpl();
        Validator validator = new ValidatorImpl();
        this.vehicleService = new VehicleServiceImpl(vehicleDAO, validator);
        this.gson = new GsonUtilImpl();
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
                out.print(gson.serialize(vehicles));
            } else if (pathInfo.startsWith("/plate/")) {
                // GET /vehicles/plate/{licensePlate}
                String licensePlate = pathInfo.substring("/plate/".length());
                // CORRIGIDO: Usando getVehicleByPlate
                Optional<Vehicle> vehicle = vehicleService.getVehicleByPlate(licensePlate);
                if (vehicle.isPresent()) {
                    out.print(gson.serialize(vehicle.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Veículo não encontrado com placa: " + licensePlate)));
                }
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
                if (vehicle.isPresent()) {
                    out.print(gson.serialize(vehicle.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Veículo não encontrado com o ID: " + id)));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID ou formato de URL inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número no GET de veículo: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de veículo: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de veículo: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado no GET de veículo: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de veículo: " + e.getMessage(), e);
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
            Vehicle newVehicle = gson.deserialize(jsonBody, Vehicle.class);
            // CORRIGIDO: Usando registerVehicle
            Vehicle createdVehicle = vehicleService.registerVehicle(newVehicle);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdVehicle));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar veículo: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar veículo: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao criar veículo: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar veículo: " + e.getMessage(), e);
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
            out.print(gson.serialize(new ErrorResponse("ID do veículo é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Vehicle updatedVehicle = gson.deserialize(jsonBody, Vehicle.class);
            updatedVehicle.setId(id);

            // CORRIGIDO: updateVehicle retorna boolean
            boolean updated = vehicleService.updateVehicle(updatedVehicle);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.serialize(updatedVehicle));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Veículo não encontrado para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID do veículo inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar veículo: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar veículo: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar veículo: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao atualizar veículo: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar veículo: " + e.getMessage(), e);
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
            out.print(gson.serialize(new ErrorResponse("ID do veículo é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = vehicleService.deleteVehicle(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Veículo não encontrado para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID do veículo inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar veículo: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar veículo: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar veículo: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado ao deletar veículo: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar veículo: " + e.getMessage(), e);
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