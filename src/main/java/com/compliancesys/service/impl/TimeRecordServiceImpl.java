package com.compliancesys.service.impl;

import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.PointRecordDAO;
import com.compliancesys.dao.impl.JourneyDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.dao.impl.PointRecordDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Journey;
import com.compliancesys.model.PointRecord;
import com.compliancesys.service.TimeRecordService; // Renomeado de PointRecordService para TimeRecordService
import com.compliancesys.model.PointType; // Para usar o enum PointType

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface TimeRecordService (anteriormente PointRecordService).
 * Contém a lógica de negócio para a entidade PointRecord, interagindo com a camada DAO.
 */
public class TimeRecordServiceImpl implements TimeRecordService { // Renomeado de PointRecordServiceImpl para TimeRecordServiceImpl

    private static final Logger LOGGER = Logger.getLogger(TimeRecordServiceImpl.class.getName());
    private final PointRecordDAO pointRecordDAO;
    private final JourneyDAO journeyDAO; // Necessário para validar a existência da jornada

    /**
     * Construtor padrão que inicializa os DAOs.
     */
    public TimeRecordServiceImpl() {
        this.pointRecordDAO = new PointRecordDAOImpl();
        this.journeyDAO = new JourneyDAOImpl();
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param pointRecordDAO A implementação de PointRecordDAO a ser utilizada.
     * @param journeyDAO A implementação de JourneyDAO a ser utilizada.
     */
    public TimeRecordServiceImpl(PointRecordDAO pointRecordDAO, JourneyDAO journeyDAO) {
        this.pointRecordDAO = pointRecordDAO;
        this.journeyDAO = journeyDAO;
    }

    @Override
    public PointRecord createPointRecord(PointRecord pointRecord) throws BusinessException {
        // Validações de negócio antes de criar o registro de ponto
        if (pointRecord.getJourneyId() <= 0) {
            throw new BusinessException("O ID da jornada é obrigatório e deve ser um valor positivo.");
        }
        if (pointRecord.getTimestamp() == null) {
            throw new BusinessException("O timestamp do registro de ponto é obrigatório.");
        }
        if (pointRecord.getPointType() == null) {
            throw new BusinessException("O tipo de ponto é obrigatório (INICIO_JORNADA, FIM_JORNADA, INICIO_REFEICAO, FIM_REFEICAO, etc.).");
        }
        if (pointRecord.getLatitude() == null || pointRecord.getLongitude() == null) {
            throw new BusinessException("Latitude e Longitude são obrigatórias para o registro de ponto.");
        }
        // Adicionar validações de formato para latitude/longitude se necessário

        try {
            // Verifica se a jornada associada existe
            Optional<Journey> existingJourney = journeyDAO.findById(pointRecord.getJourneyId());
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + pointRecord.getJourneyId() + " não encontrada. Não é possível criar o registro de ponto.");
            }

            // Lógica de validação de sequência de pontos (ex: não pode ter dois INICIO_JORNADA seguidos sem um FIM_JORNADA)
            // Esta é uma lógica de negócio complexa e pode exigir consultas adicionais ou um estado mais elaborado.
            // Por simplicidade, um exemplo básico:
            List<PointRecord> existingRecordsForJourney = pointRecordDAO.findByJourneyId(pointRecord.getJourneyId());
            if (!existingRecordsForJourney.isEmpty()) {
                PointRecord lastRecord = existingRecordsForJourney.stream()
                                            .max((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()))
                                            .orElse(null);

                if (lastRecord != null) {
                    if (pointRecord.getPointType() == PointType.INICIO_JORNADA && lastRecord.getPointType() == PointType.INICIO_JORNADA) {
                        throw new BusinessException("Não é possível registrar dois 'INICIO_JORNADA' consecutivos sem um 'FIM_JORNADA'.");
                    }
                    if (pointRecord.getPointType() == PointType.FIM_JORNADA && lastRecord.getPointType() != PointType.INICIO_JORNADA && lastRecord.getPointType() != PointType.FIM_REFEICAO) {
                        // Lógica mais complexa pode ser necessária aqui para garantir que FIM_JORNADA seja o último ponto válido
                        // ou que venha após um INICIO_JORNADA ou FIM_REFEICAO
                    }
                    // Outras validações de sequência podem ser adicionadas aqui
                }
            } else {
                // Se não há registros anteriores, o primeiro deve ser INICIO_JORNADA
                if (pointRecord.getPointType() != PointType.INICIO_JORNADA) {
                    throw new BusinessException("O primeiro registro de ponto de uma jornada deve ser 'INICIO_JORNADA'.");
                }
            }


            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            pointRecord.setCreatedAt(now);
            pointRecord.setUpdatedAt(now);

            int id = pointRecordDAO.create(pointRecord);
            if (id > 0) {
                pointRecord.setId(id);
                LOGGER.log(Level.INFO, "Registro de ponto criado com sucesso para a jornada ID: {0}, tipo: {1}", new Object[]{pointRecord.getJourneyId(), pointRecord.getPointType()});
                return pointRecord;
            } else {
                throw new BusinessException("Falha ao criar o registro de ponto. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<PointRecord> getPointRecordById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do registro de ponto deve ser um valor positivo.");
        }
        try {
            return pointRecordDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registro de ponto por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<PointRecord> getAllPointRecords() throws BusinessException {
        try {
            return pointRecordDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todos os registros de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar os registros de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<PointRecord> getPointRecordsByJourneyId(int journeyId) throws BusinessException {
        if (journeyId <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo.");
        }
        try {
            return pointRecordDAO.findByJourneyId(journeyId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar registros de ponto por ID de jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar registros de ponto por jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public PointRecord updatePointRecord(PointRecord pointRecord) throws BusinessException {
        if (pointRecord.getId() <= 0) {
            throw new BusinessException("O ID do registro de ponto deve ser um valor positivo para atualização.");
        }
        if (pointRecord.getJourneyId() <= 0) {
            throw new BusinessException("O ID da jornada é obrigatório e deve ser um valor positivo.");
        }
        if (pointRecord.getTimestamp() == null) {
            throw new BusinessException("O timestamp do registro de ponto é obrigatório.");
        }
        if (pointRecord.getPointType() == null) {
            throw new BusinessException("O tipo de ponto é obrigatório.");
        }
        if (pointRecord.getLatitude() == null || pointRecord.getLongitude() == null) {
            throw new BusinessException("Latitude e Longitude são obrigatórias para o registro de ponto.");
        }

        try {
            // Verifica se o registro de ponto a ser atualizado existe
            Optional<PointRecord> existingRecord = pointRecordDAO.findById(pointRecord.getId());
            if (existingRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + pointRecord.getId() + " não encontrado para atualização.");
            }

            // Verifica se a jornada associada existe
            Optional<Journey> existingJourney = journeyDAO.findById(pointRecord.getJourneyId());
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + pointRecord.getJourneyId() + " não encontrada. Não é possível atualizar o registro de ponto.");
            }

            // Lógica de validação de negócio para atualização (pode ser mais complexa, dependendo das regras)
            // Por exemplo, pode-se proibir a alteração do PointType ou do timestamp após a criação.
            // Para este exemplo, permitiremos a atualização, mas em um sistema real, isso pode ser restrito.

            // Define a data de atualização
            pointRecord.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            pointRecord.setCreatedAt(existingRecord.get().getCreatedAt());

            boolean updated = pointRecordDAO.update(pointRecord);
            if (updated) {
                LOGGER.log(Level.INFO, "Registro de ponto atualizado com sucesso: ID {0}", pointRecord.getId());
                return pointRecord;
            } else {
                throw new BusinessException("Falha ao atualizar o registro de ponto. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deletePointRecord(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID do registro de ponto deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se o registro de ponto existe antes de tentar deletar
            Optional<PointRecord> existingRecord = pointRecordDAO.findById(id);
            if (existingRecord.isEmpty()) {
                throw new BusinessException("Registro de ponto com ID " + id + " não encontrado para exclusão.");
            }
            // Adicionar lógica para verificar dependências (ex: se há comunicações móveis associadas)
            // Se houver, lançar BusinessException ou tratar a exclusão em cascata (se o DB permitir e for desejado)

            boolean deleted = pointRecordDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Registro de ponto com ID {0} deletado com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar registro de ponto com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar registro de ponto: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar o registro de ponto. Tente novamente mais tarde.", e);
        }
    }
}
