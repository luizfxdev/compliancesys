package com.compliancesys.controller;

import com.compliancesys.dao.CompanyDAO;
import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.impl.CompanyDAOImpl;
import com.compliancesys.dao.impl.DriverDAOImpl;
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.service.DriverService;
import com.compliancesys.service.impl.DriverServiceImpl;
import com.compliancesys.util.ConnectionFactory;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.Validator;
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.HikariCPConnectionFactory;
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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/drivers/*")
public class DriverServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DriverServlet.class.getName());
    private ConnectionFactory connectionFactory;
    private GsonUtil gsonUtil;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.connectionFactory = new HikariCPConnectionFactory();
            this.gsonUtil = new GsonUtilImpl();
            this.validator = new ValidatorImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar DriverServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar DriverServlet", e);
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
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            CompanyDAO companyDAO = new CompanyDAOImpl(conn);
            DriverService driverService = new DriverServiceImpl(driverDAO, companyDAO, validator);

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Driver> drivers = driverService.getAllDrivers();
                out.print(gsonUtil.serialize(drivers));
            } else {
                String[] splits = pathInfo.split("/");
                if (splits.length == 2) {
                    int id = Integer.parseInt(splits[1]);
                    Optional<Driver> driver = driverService.getDriverById(id);
                    if (driver.isPresent()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(gsonUtil.serialize(driver.get()));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gsonUtil.serialize(new ErrorResponse("Motorista não encontrado.")));
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gsonUtil.serialize(new ErrorResponse("Formato de URL inválido.")));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido.")));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao buscar motorista. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao buscar motorista.")));
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
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            CompanyDAO companyDAO = new CompanyDAOImpl(conn);
            DriverService driverService = new DriverServiceImpl(driverDAO, companyDAO, validator);

            Driver newDriver = gsonUtil.deserialize(request.getReader(), Driver.class);
            Driver createdDriver = driverService.registerDriver(newDriver);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonUtil.serialize(createdDriver));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao registrar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao registrar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao registrar motorista. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao registrar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao registrar motorista.")));
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
            out.print(gsonUtil.serialize(new ErrorResponse("ID do motorista é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            CompanyDAO companyDAO = new CompanyDAOImpl(conn);
            DriverService driverService = new DriverServiceImpl(driverDAO, companyDAO, validator);

            int id = Integer.parseInt(pathInfo.substring(1));
            Driver updatedDriver = gsonUtil.deserialize(request.getReader(), Driver.class);
            updatedDriver.setId(id);

            Driver result = driverService.updateDriver(updatedDriver);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonUtil.serialize(result));
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao atualizar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao atualizar motorista. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao atualizar motorista.")));
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
            out.print(gsonUtil.serialize(new ErrorResponse("ID do motorista é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try (Connection conn = connectionFactory.getConnection()) {
            DriverDAO driverDAO = new DriverDAOImpl(conn);
            CompanyDAO companyDAO = new CompanyDAOImpl(conn);
            DriverService driverService = new DriverServiceImpl(driverDAO, companyDAO, validator);

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = driverService.deleteDriver(id);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Motorista não encontrado.")));
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido ao deletar: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID inválido.")));
        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados ao deletar motorista. Tente novamente mais tarde.")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar motorista: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao deletar motorista.")));
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