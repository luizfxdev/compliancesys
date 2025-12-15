package com.compliancesys.controller;

import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.impl.VehicleServiceImpl;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/vehicles/*")
public class VehicleServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VehicleServlet.class.getName());
    private VehicleServiceImpl vehicleService;
    private GsonUtilImpl gson;

    @Override
    public void init() throws ServletException {
        try {
            Connection connection = DatabaseConfig.getInstance().getConnection();
            VehicleDAOImpl vehicleDAO = new VehicleDAOImpl(connection);
            ValidatorImpl validator = new ValidatorImpl();
            
            this.vehicleService = new VehicleServiceImpl(vehicleDAO, validator);
            this.gson = new GsonUtilImpl();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar VehicleServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar VehicleServlet", e);
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
                List<Vehicle> vehicles = vehicleService.getAllVehicles();
                out.print(gson.serialize(vehicles));
            } else if (pathInfo.startsWith("/plate/")) {
                String licensePlate = pathInfo.substring("/plate/".length());
                Vehicle vehicle = vehicleService.getVehicleByPlate(licensePlate);
                
                if (vehicle != null) {
                    out.print(gson.serialize(vehicle));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Veículo não encontrado com placa: " + licensePlate)));
                }
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Vehicle vehicle = vehicleService.getVehicleById(id);
                
                if (vehicle != null) {
                    out.print(gson.serialize(vehicle));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.serialize(new ErrorResponse("Veículo não encontrado")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
            LOGGER.log(Level.SEVERE, "Erro de SQL: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado: " + e.getMessage(), e);
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
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Vehicle newVehicle = gson.deserialize(jsonBody, Vehicle.class);
            Vehicle createdVehicle = vehicleService.registerVehicle(newVehicle);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.serialize(createdVehicle));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar veículo: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar veículo: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar veículo: " + e.getMessage(), e);
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
            out.print(gson.serialize(new ErrorResponse("ID do veículo é obrigatório")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            Vehicle updatedVehicle = gson.deserialize(jsonBody, Vehicle.class);
            updatedVehicle.setId(id);

            boolean updated = vehicleService.updateVehicle(updatedVehicle);
            
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.serialize(updatedVehicle));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.serialize(new ErrorResponse("Veículo não encontrado")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
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
            out.print(gson.serialize(new ErrorResponse("ID do veículo é obrigatório")));
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
                out.print(gson.serialize(new ErrorResponse("Veículo não encontrado")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse("ID inválido")));
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro de banco de dados")));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.serialize(new ErrorResponse("Erro inesperado")));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
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