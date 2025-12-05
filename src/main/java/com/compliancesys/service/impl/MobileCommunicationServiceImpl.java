package com.compliancesys.service.impl;

import com.compliancesys.dao.MobileCommunicationDAO;
import com.compliancesys.dao.TimeRecordDAO; // CORRIGIDO: Para validar a existência do registro de ponto
import com.compliancesys.dao.impl.MobileCommunicationDAOImpl;
import com.compliancesys.dao.impl.TimeRecordDAOImpl; // CORRIGIDO: Para validar a existência do registro de ponto
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.MobileCommunication;
import com.compliancesys.model.TimeRecord; // CORRIGIDO: Para validar a existência do registro de ponto
import com.compliancesys.service.MobileCommunicationService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface MobileCommunicationService.
 * Contém a lógica de negócio para a entidade MobileCommunication, interagindo com a camada DAO.
 */
public class MobileCommunicationServiceImpl implements MobileCommunicationService {

    private static final Logger LOGGER = Logger.getLogger(MobileCommunicationServiceImpl.class.getName());
    private final MobileCommunicationDAO mobileCommunicationDAO;
    private final TimeRecordDAO timeRecordDAO; // CORRIGIDO: Necessário para validar a existência do registro de ponto

    /**
     * Construtor padrão que inicializa os DAOs.
     */
    public MobileCommunicationServiceImpl() {
        this.mobileCommunicationDAO = new MobileCommunicationDAOImpl();
        this.timeRecordDAO = new TimeRecordDAOImpl(); // CORRIGIDO
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param mobileCommunicationDAO A implementação de MobileCommunicationDAO a ser utilizada.
     * @param timeRecordDAO A implementação de TimeRecordDAO a ser utilizada. // CORRIGIDO
     */
    public MobileCommunicationServiceImpl(MobileCommunicationDAO mobileCommunicationDAO, TimeRecordDAO timeRecordDAO) { // CORRIGIDO
        this.mobileCommunicationDAO = mobileCommunicationDAO;
        this.timeRecordDAO = timeRecordDAO; // CORRIGIDO
    }

    @Override
    public int registerMobileCommunication(MobileCommunication communication) throws BusinessException { // CORRIGIDO: Assinatura do método
        // Validações de negócio antes de criar a comunicação móvel
        if (communication.getRecordId() <= 0) {
            throw new BusinessException("O ID do registro de ponto é obrigatório e deve ser um valor positivo.");
        }
        if (communication.getTimestamp() == null) {
            throw new BusinessException("O timestamp da comunicação é obrigatório.");
        }
        // Os campos latitude e longitude podem ser nulos dependendo da regra de negócio,
        // mas se forem obrigatórios, a validação abaixo é válida.
        // Se não forem obrigatórios, remova esta validação.
        if (communication.getLatitude() == null || communication.getLongitude() == null) {
            throw new BusinessException("Latitude e Longitude são obrigatórias.");
        }
        // Adicionar validações de formato para latitude/longitude se necessário

        try {
            // Verifica se o registro de ponto associado existe
            Optional<TimeRecord> existingTimeRecord = timeRecordDAO.findById(communication.getRecordId()); // CORRIGIDO: TimeRecord
            if (existingTimeRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + communication.getRecordId() + " não encontrado. Não é possível criar a comunicação móvel.");
            }

            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            communication.setCreatedAt(now);
            communication.setUpdatedAt(now);

            int id = mobileCommunicationDAO.create(communication);
            if (id > 0) {
                communication.setId(id);
                LOGGER.log(Level.INFO, "Comunicação móvel criada com sucesso para o registro de ponto ID: {0}", communication.getRecordId());
                return id; // CORRIGIDO: Retorna o ID
            } else {
                throw new BusinessException("Falha ao criar a comunicação móvel. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar a comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<MobileCommunication> getMobileCommunicationById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da comunicação móvel deve ser um valor positivo.");
        }
        try {
            return mobileCommunicationDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicação móvel por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getAllMobileCommunications() throws BusinessException {
        try {
            return mobileCommunicationDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as comunicações móveis: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar as comunicações móveis. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<MobileCommunication> getMobileCommunicationsByRecordId(int recordId) throws BusinessException {
        if (recordId <= 0) {
            throw new BusinessException("O ID do registro de ponto deve ser um valor positivo.");
        }
        try {
            return mobileCommunicationDAO.findByRecordId(recordId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar comunicações móveis por ID de registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar comunicações móveis por registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean updateMobileCommunication(MobileCommunication communication) throws BusinessException { // CORRIGIDO: Assinatura do método
        if (communication.getId() <= 0) {
            throw new BusinessException("O ID da comunicação móvel deve ser um valor positivo para atualização.");
        }
        if (communication.getRecordId() <= 0) {
            throw new BusinessException("O ID do registro de ponto é obrigatório e deve ser um valor positivo.");
        }
        if (communication.getTimestamp() == null) {
            throw new BusinessException("O timestamp da comunicação é obrigatório.");
        }
        if (communication.getLatitude() == null || communication.getLongitude() == null) {
            throw new BusinessException("Latitude e Longitude são obrigatórias.");
        }

        try {
            // Verifica se a comunicação móvel a ser atualizada existe
            Optional<MobileCommunication> existingCommunication = mobileCommunicationDAO.findById(communication.getId());
            if (existingCommunication.isEmpty()) {
                throw new BusinessException("Comunicação móvel com ID " + communication.getId() + " não encontrada para atualização.");
            }

            // Verifica se o registro de ponto associado existe
            Optional<TimeRecord> existingTimeRecord = timeRecordDAO.findById(communication.getRecordId()); // CORRIGIDO: TimeRecord
            if (existingTimeRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + communication.getRecordId() + " não encontrado. Não é possível atualizar a comunicação móvel.");
            }

            // Define a data de atualização
            communication.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            communication.setCreatedAt(existingCommunication.get().getCreatedAt());

            boolean updated = mobileCommunicationDAO.update(communication);
            if (updated) {
                LOGGER.log(Level.INFO, "Comunicação móvel atualizada com sucesso: ID {0}", communication.getId());
                return true; // CORRIGIDO: Retorna true
            } else {
                throw new BusinessException("Falha ao atualizar a comunicação móvel. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar a comunicação móvel. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteMobileCommunication(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da comunicação móvel deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se a comunicação móvel existe antes de tentar deletar
            Optional<MobileCommunication> existingCommunication = mobileCommunicationDAO.findById(id);
            if (existingCommunication.isEmpty()) {
                throw new BusinessException("Comunicação móvel com ID " + id + " não encontrada para exclusão.");
            }

            boolean deleted = mobileCommunicationDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Comunicação móvel com ID {0} deletada com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar comunicação móvel com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar comunicação móvel: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar a comunicação móvel. Tente novamente mais tarde.", e);
        }
    }
}
