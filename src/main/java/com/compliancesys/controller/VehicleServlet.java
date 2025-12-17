package com.compliancesys.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;
import com.compliancesys.service.VehicleService;
import com.compliancesys.service.impl.VehicleServiceImpl;
import com.compliancesys.util.ConnectionFactory;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.HikariCPConnectionFactory;
import com.compliancesys.util.impl.ValidatorImpl;

@WebServlet("/api/vehicles/*")
public class VehicleServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(VehicleServlet.class.getName());
    private VehicleService vehicleService;
    private GsonUtil gsonUtil;
    private ConnectionFactory connectionFactory;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.connectionFactory = new HikariCPConnectionFactory();
            VehicleDAO vehicleDAO = new VehicleDAOImpl(connectionFactory);
            CompanyDAO companyDAO = new CompanyDAOImpl(connectionFactory);
            Validator validator = new ValidatorImpl();
            this.vehicleService = new VehicleServiceImpl(vehicleDAO, companyDAO, validator);
            this.gsonUtil = new GsonUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar VehicleServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar VehicleServlet", e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.connectionFactory != null) {
            this.connectionFactory.closeDataSource();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Vehicle> vehicles = vehicleService.getAllVehicles();
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonUtil.serialize(vehicles));
            } else {
                String[] splits = pathInfo.split("/");
                if (splits.length == 2) {
                    try {
                        int id = Integer.parseInt(splits[1]);
                        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
                        if (vehicle.isPresent()) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.print(gsonUtil.serialize(vehicle.get()));
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print(gsonUtil.serialize(new ErrorResponse("Veículo não encontrado")));
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gsonUtil.serialize(new ErrorResponse("ID inválido")));
                        LOGGER.log(Level.WARNING, "ID inválido ao buscar veículo: " + e.getMessage(), e);
                    }
                } else if (splits.length == 3 && "plate".equals(splits[1])) {
                    String plate = splits[2];
                    Optional<Vehicle> vehicle = vehicleService.getVehicleByPlate(plate);
                    if (vehicle.isPresent()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(gsonUtil.serialize(vehicle.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gsonUtil.serialize(new ErrorResponse("Veículo não encontrado para a placa: " + plate)));
                    }
                } else if (splits.length == 3 && "company".equals(splits[1])) {
                    try {
                        int companyId = Integer.parseInt(splits[2]);
                        List<Vehicle> vehicles = vehicleService.getVehiclesByCompanyId(companyId);
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(gsonUtil.serialize(vehicles));
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gsonUtil.serialize(new ErrorResponse("ID da empresa inválido")));
                        LOGGER.log(Level.WARNING, "ID da empresa inválido ao buscar veículos: " + e.getMessage(), e);
                    }
                } else if (splits.length == 3 && "model".equals(splits[1])) {
                    String model = splits[2];
                    List<Vehicle> vehicles = vehicleService.getVehiclesByModel(model);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gsonUtil.serialize(vehicles));
                } else if (splits.length == 3 && "year".equals(splits[1])) {
                    try {
                        int year = Integer.parseInt(splits[2]);
                        List<Vehicle> vehicles = vehicleService.getVehiclesByYear(year);
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(gsonUtil.serialize(vehicles));
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gsonUtil.serialize(new ErrorResponse("Ano inválido")));
                        LOGGER.log(Level.WARNING, "Ano inválido ao buscar veículos: " + e.getMessage(), e);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonUtil.serialize(new ErrorResponse("Requisição inválida")));
                }
            }
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao buscar veículo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar veículo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar veículo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Vehicle newVehicle = gsonUtil.deserialize(request.getReader(), Vehicle.class);
            Vehicle createdVehicle = vehicleService.createVehicle(newVehicle);

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonUtil.serialize(createdVehicle));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar veículo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar veículo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar veículo: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID do veículo é obrigatório")));
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            Vehicle updatedVehicle = gsonUtil.deserialize(request.getReader(), Vehicle.class);
            updatedVehicle.setId(id);

            Vehicle result = vehicleService.updateVehicle(updatedVehicle);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonUtil.serialize(result));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID do veículo é obrigatório")));
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
                out.print(gsonUtil.serialize(new ErrorResponse("Veículo não encontrado")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado")));
        } finally {
            out.flush();
        }
    }

    private static class ErrorResponse {
        private final String message;
        private final LocalDateTime timestamp;

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