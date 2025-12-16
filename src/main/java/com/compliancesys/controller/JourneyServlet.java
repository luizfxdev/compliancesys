package com.compliancesys.controller;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.VehicleDAO;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.dao.impl.JourneyDAOImpl;
import com.compliancesys.dao.impl.VehicleDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.service.JourneyService;
import com.compliancesys.service.impl.JourneyServiceImpl;
import com.compliancesys.util.ConnectionFactory;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.TimeUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.HikariCPConnectionFactory;
import com.compliancesys.util.impl.TimeUtilImpl;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/journeys/*")
public class JourneyServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(JourneyServlet.class.getName());
    private ConnectionFactory connectionFactory;
    private GsonUtil gsonUtil;
    private Validator validator;
    private TimeUtil timeUtil;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.connectionFactory = new HikariCPConnectionFactory();
            this.gsonUtil = new GsonUtilImpl();
            this.validator = new ValidatorImpl();
            this.timeUtil = new TimeUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar JourneyServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar JourneyServlet", e);
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

        try (Connection conn = connectionFactory.getConnection()) {
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            VehicleDAO vehicleDAO = new VehicleDAOImpl(conn);
            JourneyService journeyService = new JourneyServiceImpl(journeyDAO, driverDAO, vehicleDAO, validator, timeUtil);

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Journey> journeys = journeyService.getAllJourneys();
                out.print(gsonUtil.serialize(journeys));
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    if (pathParts.length == 2) {
                        String identifier = pathParts[1];
                        if (identifier.matches("\\d+")) {
                            int id = Integer.parseInt(identifier);
                            Optional<Journey> journeyOptional = journeyService.getJourneyById(id);
                            if (journeyOptional.isPresent()) {
                                out.print(gsonUtil.serialize(journeyOptional.get()));
                            } else {
                                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                out.print(gsonUtil.serialize(new ErrorResponse("Jornada não encontrada.")));
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.print(gsonUtil.serialize(new ErrorResponse("Identificador inválido.")));
                        }
                    } else if (pathParts.length == 3 && "driver".equals(pathParts[1])) {
                        int driverId = Integer.parseInt(pathParts[2]);
                        List<Journey> journeys = journeyService.getJourneysByDriverId(driverId);
                        out.print(gsonUtil.serialize(journeys));
                    } else if (pathParts.length == 3 && "vehicle".equals(pathParts[1])) {
                        int vehicleId = Integer.parseInt(pathParts[2]);
                        List<Journey> journeys = journeyService.getJourneysByVehicleId(vehicleId);
                        out.print(gsonUtil.serialize(journeys));
                    } else if (pathParts.length == 3 && "company".equals(pathParts[1])) {
                        int companyId = Integer.parseInt(pathParts[2]);
                        List<Journey> journeys = journeyService.getJourneysByCompanyId(companyId);
                        out.print(gsonUtil.serialize(journeys));
                    } else if (pathParts.length == 5 && "driver".equals(pathParts[1]) && "date".equals(pathParts[3])) {
                        int driverId = Integer.parseInt(pathParts[2]);
                        LocalDate date = LocalDate.parse(pathParts[4]);
                        Optional<Journey> journeyOptional = journeyService.getJourneyByDriverIdAndDate(driverId, date);
                        if (journeyOptional.isPresent()) {
                            out.print(gsonUtil.serialize(journeyOptional.get()));
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print(gsonUtil.serialize(new ErrorResponse("Jornada não encontrada para o motorista e data especificados.")));
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(gsonUtil.serialize(new ErrorResponse("Formato de URL inválido para busca de jornada.")));
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonUtil.serialize(new ErrorResponse("Formato de URL inválido para busca de jornada.")));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID ou parâmetro numérico inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID ou parâmetro numérico inválido.")));
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Formato de data inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("Formato de data inválido. Use YYYY-MM-DD.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao buscar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao buscar jornada. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao buscar jornada.")));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = connectionFactory.getConnection()) {
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            VehicleDAO vehicleDAO = new VehicleDAOImpl(conn);
            JourneyService journeyService = new JourneyServiceImpl(journeyDAO, driverDAO, vehicleDAO, validator, timeUtil);

            Journey newJourney = gsonUtil.deserialize(request.getReader(), Journey.class);
            Journey createdJourney = journeyService.createJourney(newJourney);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonUtil.serialize(createdJourney));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao criar jornada. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao criar jornada.")));
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
            out.print(gsonUtil.serialize(new ErrorResponse("ID da jornada é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            VehicleDAO vehicleDAO = new VehicleDAOImpl(conn);
            JourneyService journeyService = new JourneyServiceImpl(journeyDAO, driverDAO, vehicleDAO, validator, timeUtil);

            int id = Integer.parseInt(pathInfo.substring(1));
            Journey updatedJourney = gsonUtil.deserialize(request.getReader(), Journey.class);
            updatedJourney.setId(id);

            Journey result = journeyService.updateJourney(updatedJourney);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonUtil.serialize(result));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao atualizar jornada. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar jornada: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao atualizar jornada.")));
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
            out.print(gsonUtil.serialize(new ErrorResponse("ID da jornada é obrigatório")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            JourneyDAO journeyDAO = new JourneyDAOImpl(conn);
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            VehicleDAO vehicleDAO = new VehicleDAOImpl(conn);
            JourneyService journeyService = new JourneyServiceImpl(journeyDAO, driverDAO, vehicleDAO, validator, timeUtil);

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = journeyService.deleteJourney(id);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Jornada não encontrada")));
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