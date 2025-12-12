package com.compliancesys.controller;

<<<<<<< Updated upstream
=======
import com.compliancesys.config.DatabaseConfig;
import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
>>>>>>> Stashed changes
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.service.MobileCommunicationService;
import com.compliancesys.service.impl.MobileCommunicationServiceImpl;
import com.compliancesys.util.GsonUtil;
<<<<<<< Updated upstream
import com.compliancesys.util.impl.GsonUtilImpl;

=======
import com.compliancesys.util.Validator; // Adicionado para MobileCommunicationServiceImpl
import com.compliancesys.util.impl.GsonUtilImpl;
import com.compliancesys.util.impl.ValidatorImpl; // Adicionado para MobileCommunicationServiceImpl
>>>>>>> Stashed changes
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

<<<<<<< Updated upstream
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
=======
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime; // Adicionado para ErrorResponse
>>>>>>> Stashed changes
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
<<<<<<< Updated upstream

@WebServlet("/api/mobile-communications/*")
=======
import java.util.stream.Collectors;

/**
 * Servlet para gerenciar operações CRUD de comunicações móveis.
 * Responde a requisições HTTP para /mobilecommunications.
 */
@WebServlet("/mobilecommunications/*")
>>>>>>> Stashed changes
public class MobileCommunicationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationServlet.class.getName());
    private MobileCommunicationService mobileCommunicationService;
    private GsonUtil gsonUtil;

<<<<<<< Updated upstream
    public MobileCommunicationServlet() {
        this.mobileCommunicationService = new MobileCommunicationServiceImpl();
        this.gsonUtil = new GsonUtilImpl();
    }

    public MobileCommunicationServlet(MobileCommunicationService mobileCommunicationService, GsonUtil gsonUtil) {
        this.mobileCommunicationService = mobileCommunicationService;
        this.gsonUtil = gsonUtil;
=======
    @Override
    public void init() throws ServletException {
        try {
            DataSource dataSource = DatabaseConfig.getInstance().getDataSource();
            MobileCommunicationDAO mobileCommunicationDAO = new MobileCommunicationDAOImpl(dataSource);
            Validator validator = new ValidatorImpl(); // Instancia Validator
            this.mobileCommunicationService = new MobileCommunicationServiceImpl(mobileCommunicationDAO, validator); // Injeta DAO e Validator
            this.gsonSerializer = new GsonUtilImpl();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar MobileCommunicationServlet: " + e.getMessage(), e);
            throw new ServletException("Erro ao inicializar MobileCommunicationServlet", e);
        }
>>>>>>> Stashed changes
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
<<<<<<< Updated upstream

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<MobileCommunication> mobileCommunications = mobileCommunicationService.getAllMobileCommunications();
                out.print(gsonUtil.serialize(mobileCommunications));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<MobileCommunication> mobileCommunication = mobileCommunicationService.getMobileCommunicationById(id);
                if (mobileCommunication.isPresent()) {
                    out.print(gsonUtil.serialize(mobileCommunication.get()));
=======
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /mobilecommunications - Retorna todas as comunicações móveis
                List<MobileCommunication> communications = mobileCommunicationService.getAllMobileCommunications();
                out.print(gsonSerializer.serialize(communications));
            } else {
                // GET /mobilecommunications/{id} - Retorna uma comunicação móvel específica
                int commId = Integer.parseInt(pathInfo.substring(1));
                Optional<MobileCommunication> communication = mobileCommunicationService.getMobileCommunicationById(commId);
                if (communication.isPresent()) {
                    out.print(gsonSerializer.serialize(communication.get()));
>>>>>>> Stashed changes
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonUtil.serialize(new ErrorResponse("Comunicação móvel não encontrada.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gsonUtil.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao buscar comunicação móvel: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao buscar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao buscar comunicação móvel: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar comunicação móvel: " + e.getMessage(), e);
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID inválido no caminho da URL.")));
            LOGGER.log(Level.WARNING, "ID inválido no caminho da URL para GET de comunicação móvel: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio no GET de comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL no GET de comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado no GET de comunicação móvel: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro inesperado no GET de comunicação móvel: " + e.getMessage(), e);
>>>>>>> Stashed changes
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

<<<<<<< Updated upstream
        try (BufferedReader reader = request.getReader()) {
            MobileCommunication mobileCommunication = gsonUtil.deserialize(reader, MobileCommunication.class);
            MobileCommunication createdCommunication = mobileCommunicationService.createMobileCommunication(mobileCommunication);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonUtil.serialize(createdCommunication));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao criar comunicação móvel: " + e.getMessage())));
=======
        try {
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            MobileCommunication newCommunication = gsonSerializer.deserialize(jsonBody, MobileCommunication.class);
            MobileCommunication createdCommunication = mobileCommunicationService.registerMobileCommunication(newCommunication);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(createdCommunication));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao criar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar comunicação móvel: " + e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.SEVERE, "Erro inesperado ao criar comunicação móvel: " + e.getMessage(), e);
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
            out.print(gsonUtil.serialize(new ErrorResponse("ID da comunicação móvel é obrigatório para atualização.")));
            out.flush();
            return;
        }

<<<<<<< Updated upstream
        try (BufferedReader reader = request.getReader()) {
            int id = Integer.parseInt(pathInfo.substring(1));
            MobileCommunication mobileCommunication = gsonUtil.deserialize(reader, MobileCommunication.class);
            mobileCommunication.setId(id);

            MobileCommunication updatedCommunication = mobileCommunicationService.updateMobileCommunication(mobileCommunication);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gsonUtil.serialize(updatedCommunication));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar comunicação móvel: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao atualizar comunicação móvel: " + e.getMessage())));
=======
        try {
            int commId = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = request.getReader().lines().collect(Collectors.joining());
            MobileCommunication updatedCommunication = gsonSerializer.deserialize(jsonBody, MobileCommunication.class);
            updatedCommunication.setId(commId); // Garante que o ID do path seja usado

            boolean updated = mobileCommunicationService.updateMobileCommunication(updatedCommunication);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(updatedCommunication));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Comunicação móvel não encontrada para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao atualizar comunicação móvel: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao atualizar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar comunicação móvel: " + e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar comunicação móvel: " + e.getMessage(), e);
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
            out.print(gsonUtil.serialize(new ErrorResponse("ID da comunicação móvel é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
<<<<<<< Updated upstream
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = mobileCommunicationService.deleteMobileCommunication(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
=======
            int commId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = mobileCommunicationService.deleteMobileCommunication(commId);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
>>>>>>> Stashed changes
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Comunicação móvel não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
<<<<<<< Updated upstream
            out.print(gsonUtil.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar comunicação móvel: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonUtil.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonUtil.serialize(new ErrorResponse("Erro inesperado ao deletar comunicação móvel: " + e.getMessage())));
=======
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
            LOGGER.log(Level.WARNING, "Erro de formato de número ao deletar comunicação móvel: " + e.getMessage(), e);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
            LOGGER.log(Level.WARNING, "Erro de negócio ao deletar comunicação móvel: " + e.getMessage(), e);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro de banco de dados: " + e.getMessage())));
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar comunicação móvel: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar comunicação móvel: " + e.getMessage())));
>>>>>>> Stashed changes
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar comunicação móvel: " + e.getMessage(), e);
        } finally {
            out.flush();
        }
    }

<<<<<<< Updated upstream
    public static class ErrorResponse {
=======
    private static class ErrorResponse {
>>>>>>> Stashed changes
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
