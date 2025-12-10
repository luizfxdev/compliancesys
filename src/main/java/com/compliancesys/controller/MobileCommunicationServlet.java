package com.compliancesys.controller;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.service.MobileCommunicationService;
import com.compliancesys.service.impl.MobileCommunicationServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.impl.GsonUtilImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/mobile-communications/*")
public class MobileCommunicationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationServlet.class.getName());
    private MobileCommunicationService mobileCommunicationService;
    private GsonUtil gsonUtil;

    public MobileCommunicationServlet() {
        this.mobileCommunicationService = new MobileCommunicationServiceImpl();
        this.gsonUtil = new GsonUtilImpl();
    }

    public MobileCommunicationServlet(MobileCommunicationService mobileCommunicationService, GsonUtil gsonUtil) {
        this.mobileCommunicationService = mobileCommunicationService;
        this.gsonUtil = gsonUtil;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<MobileCommunication> mobileCommunications = mobileCommunicationService.getAllMobileCommunications();
                out.print(gsonUtil.serialize(mobileCommunications));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Optional<MobileCommunication> mobileCommunication = mobileCommunicationService.getMobileCommunicationById(id);
                if (mobileCommunication.isPresent()) {
                    out.print(gsonUtil.serialize(mobileCommunication.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonUtil.serialize(new ErrorResponse("Comunicação móvel não encontrada.")));
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

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
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = mobileCommunicationService.deleteMobileCommunication(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonUtil.serialize(new ErrorResponse("Comunicação móvel não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
            LOGGER.log(Level.SEVERE, "Erro inesperado ao deletar comunicação móvel: " + e.getMessage(), e);
        } finally {
            out.flush();
        }
    }

    public static class ErrorResponse {
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
