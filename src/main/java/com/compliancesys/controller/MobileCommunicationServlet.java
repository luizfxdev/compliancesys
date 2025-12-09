package com.compliancesys.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter; // Assumindo uma implementação
import java.sql.SQLException;
import java.util.List; // Assumindo uma implementação
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.compliancesys.model.MobileCommunication;
import com.compliancesys.service.MobileCommunicationService;
import com.compliancesys.service.impl.MobileCommunicationServiceImpl;
import com.compliancesys.util.GsonUtil;
import com.compliancesys.util.impl.GsonUtilImpl;

/**
 * Servlet para gerenciar operações CRUD de comunicações móveis.
 * Responde a requisições HTTP para /mobilecommunications.
 */
@WebServlet("/mobilecommunications/*") // Adicionado /* para permitir pathInfo
public class MobileCommunicationServlet extends HttpServlet {

    private MobileCommunicationService mobileCommunicationService;
    private GsonUtil gsonSerializer;

    @Override
    public void init() throws ServletException {
        // Instanciando diretamente para o exemplo. Em um projeto real, use injeção de dependência.
        this.mobileCommunicationService = new MobileCommunicationServiceImpl(); // Você precisará criar MobileCommunicationServiceImpl
        this.gsonSerializer = new GsonUtilImpl(); // Você precisará criar GsonUtilImpl
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // /mobilecommunications/{id} ou /mobilecommunications/record/{recordId}

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /mobilecommunications - Retorna todas as comunicações
                List<MobileCommunication> communications = mobileCommunicationService.getAllMobileCommunications();
                out.print(gsonSerializer.serialize(communications));
            } else if (pathInfo.startsWith("/record/")) {
                // GET /mobilecommunications/record/{recordId} - Retorna comunicações para um TimeRecord específico
                int recordId = Integer.parseInt(pathInfo.substring("/record/".length()));
                List<MobileCommunication> communications = mobileCommunicationService.getMobileCommunicationsByRecordId(recordId);
                out.print(gsonSerializer.serialize(communications));
            } else {
                // GET /mobilecommunications/{id} - Retorna uma comunicação específica
                int commId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
                Optional<MobileCommunication> communication = mobileCommunicationService.getMobileCommunicationById(commId);
                if (communication.isPresent()) {
                    out.print(gsonSerializer.serialize(communication.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gsonSerializer.serialize(new ErrorResponse("Comunicação móvel não encontrada.")));
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
            // Lê o corpo completo da requisição
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            MobileCommunication communication = gsonSerializer.deserialize(sb.toString(), MobileCommunication.class);

            int newCommId = mobileCommunicationService.registerMobileCommunication(communication);
            communication.setId(newCommId); // Define o ID gerado no objeto
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gsonSerializer.serialize(communication));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao registrar comunicação móvel: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Erros inesperados no servidor
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao criar comunicação móvel: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da comunicação móvel é obrigatório para atualização.")));
            out.flush();
            return;
        }

        try {
            int commId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial

            // Lê o corpo completo da requisição
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            MobileCommunication communication = gsonSerializer.deserialize(sb.toString(), MobileCommunication.class);
            communication.setId(commId); // Garante que o ID do path seja usado

            if (mobileCommunicationService.updateMobileCommunication(communication)) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gsonSerializer.serialize(communication));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Comunicação móvel não encontrada para atualização.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse(e.getMessage())));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao atualizar comunicação móvel: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Erros inesperados no servidor
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao atualizar comunicação móvel: " + e.getMessage())));
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
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da comunicação móvel é obrigatório para exclusão.")));
            out.flush();
            return;
        }

        try {
            int commId = Integer.parseInt(pathInfo.substring(1)); // Remove a barra inicial
            if (mobileCommunicationService.deleteMobileCommunication(commId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content para exclusão bem-sucedida
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gsonSerializer.serialize(new ErrorResponse("Comunicação móvel não encontrada para exclusão.")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gsonSerializer.serialize(new ErrorResponse("ID da comunicação móvel inválido.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro ao deletar comunicação móvel: " + e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Erros inesperados no servidor
            out.print(gsonSerializer.serialize(new ErrorResponse("Erro inesperado ao deletar comunicação móvel: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    // Classe auxiliar para padronizar respostas de erro
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
