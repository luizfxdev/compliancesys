package com.compliancesys.service.impl;

import com.compliancesys.dao.DriverDAO;
import com.compliancesys.dao.JourneyDAO;
import com.compliancesys.dao.impl.DriverDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.dao.impl.JourneyDAOImpl; // Assumindo que você tem essa implementação
import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;
import com.compliancesys.model.Journey;
import com.compliancesys.service.JourneyService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementação da interface JourneyService.
 * Contém a lógica de negócio para a entidade Journey, interagindo com a camada DAO.
 */
public class JourneyServiceImpl implements JourneyService {

    private static final Logger LOGGER = Logger.getLogger(JourneyServiceImpl.class.getName());
    private final JourneyDAO journeyDAO;
    private final DriverDAO driverDAO; // Necessário para validar a existência do motorista

    /**
     * Construtor padrão que inicializa os DAOs.
     */
    public JourneyServiceImpl() {
        this.journeyDAO = new JourneyDAOImpl();
        this.driverDAO = new DriverDAOImpl();
    }

    /**
     * Construtor para injeção de dependência, útil para testes.
     * @param journeyDAO A implementação de JourneyDAO a ser utilizada.
     * @param driverDAO A implementação de DriverDAO a ser utilizada.
     */
    public JourneyServiceImpl(JourneyDAO journeyDAO, DriverDAO driverDAO) {
        this.journeyDAO = journeyDAO;
        this.driverDAO = driverDAO;
    }

    @Override
    public Journey createJourney(Journey journey) throws BusinessException {
        // Validações de negócio antes de criar a jornada
        if (journey.getDriverId() <= 0) {
            throw new BusinessException("O ID do motorista é obrigatório e deve ser um valor positivo.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("A data da jornada é obrigatória.");
        }
        if (journey.getStartLocation() == null || journey.getStartLocation().trim().isEmpty()) {
            throw new BusinessException("O local de início da jornada não pode ser vazio.");
        }
        if (journey.getEndLocation() == null || journey.getEndLocation().trim().isEmpty()) {
            throw new BusinessException("O local de fim da jornada não pode ser vazio.");
        }

        try {
            // Verifica se o motorista associado existe
            Optional<Driver> existingDriver = driverDAO.findById(journey.getDriverId());
            if (existingDriver.isEmpty()) {
                throw new BusinessException("Motorista com ID " + journey.getDriverId() + " não encontrado. Não é possível criar a jornada.");
            }

            // Verifica se já existe uma jornada para o mesmo motorista na mesma data
            Optional<Journey> existingJourney = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
            if (existingJourney.isPresent()) {
                throw new BusinessException("Já existe uma jornada cadastrada para o motorista ID " + journey.getDriverId() + " na data " + journey.getJourneyDate() + ".");
            }

            // Define as datas de criação e atualização
            LocalDateTime now = LocalDateTime.now();
            journey.setCreatedAt(now);
            journey.setUpdatedAt(now);

            int id = journeyDAO.create(journey);
            if (id > 0) {
                journey.setId(id);
                LOGGER.log(Level.INFO, "Jornada criada com sucesso para o motorista ID: {0} na data: {1}", new Object[]{journey.getDriverId(), journey.getJourneyDate()});
                return journey;
            } else {
                throw new BusinessException("Falha ao criar a jornada. Nenhum ID retornado.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao criar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar a jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo.");
        }
        try {
            return journeyDAO.findById(id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por ID: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo.");
        }
        if (journeyDate == null) {
            throw new BusinessException("A data da jornada é obrigatória.");
        }
        try {
            return journeyDAO.findByDriverIdAndDate(driverId, journeyDate);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornada por motorista e data: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar a jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getAllJourneys() throws BusinessException {
        try {
            return journeyDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar todas as jornadas: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao listar as jornadas. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public List<Journey> getJourneysByDriverId(int driverId) throws BusinessException {
        if (driverId <= 0) {
            throw new BusinessException("O ID do motorista deve ser um valor positivo.");
        }
        try {
            return journeyDAO.findByDriverId(driverId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao buscar jornadas por ID de motorista: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar jornadas por motorista. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public Journey updateJourney(Journey journey) throws BusinessException {
        if (journey.getId() <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo para atualização.");
        }
        if (journey.getDriverId() <= 0) {
            throw new BusinessException("O ID do motorista é obrigatório e deve ser um valor positivo.");
        }
        if (journey.getJourneyDate() == null) {
            throw new BusinessException("A data da jornada é obrigatória.");
        }
        if (journey.getStartLocation() == null || journey.getStartLocation().trim().isEmpty()) {
            throw new BusinessException("O local de início da jornada não pode ser vazio.");
        }
        if (journey.getEndLocation() == null || journey.getEndLocation().trim().isEmpty()) {
            throw new BusinessException("O local de fim da jornada não pode ser vazio.");
        }

        try {
            // Verifica se a jornada a ser atualizada existe
            Optional<Journey> existingJourneyById = journeyDAO.findById(journey.getId());
            if (existingJourneyById.isEmpty()) {
                throw new BusinessException("Jornada com ID " + journey.getId() + " não encontrada para atualização.");
            }

            // Verifica se o motorista associado existe
            Optional<Driver> existingDriver = driverDAO.findById(journey.getDriverId());
            if (existingDriver.isEmpty()) {
                throw new BusinessException("Motorista com ID " + journey.getDriverId() + " não encontrado. Não é possível atualizar a jornada.");
            }

            // Verifica se a atualização do driverId e journeyDate não cria uma duplicidade
            Optional<Journey> existingJourneyByDriverAndDate = journeyDAO.findByDriverIdAndDate(journey.getDriverId(), journey.getJourneyDate());
            if (existingJourneyByDriverAndDate.isPresent() && existingJourneyByDriverAndDate.get().getId() != journey.getId()) {
                throw new BusinessException("Já existe outra jornada cadastrada para o motorista ID " + journey.getDriverId() + " na data " + journey.getJourneyDate() + ".");
            }

            // Define a data de atualização
            journey.setUpdatedAt(LocalDateTime.now());
            // Mantém a data de criação original
            journey.setCreatedAt(existingJourneyById.get().getCreatedAt());

            boolean updated = journeyDAO.update(journey);
            if (updated) {
                LOGGER.log(Level.INFO, "Jornada atualizada com sucesso: ID {0}", journey.getId());
                return journey;
            } else {
                throw new BusinessException("Falha ao atualizar a jornada. Nenhuma linha afetada.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao atualizar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar a jornada. Tente novamente mais tarde.", e);
        }
    }

    @Override
    public boolean deleteJourney(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("O ID da jornada deve ser um valor positivo para exclusão.");
        }
        try {
            // Opcional: Verificar se a jornada existe antes de tentar deletar
            Optional<Journey> existingJourney = journeyDAO.findById(id);
            if (existingJourney.isEmpty()) {
                throw new BusinessException("Jornada com ID " + id + " não encontrada para exclusão.");
            }
            // Adicionar lógica para verificar dependências (ex: se há registros de ponto ou auditorias associadas)
            // Se houver, lançar BusinessException ou tratar a exclusão em cascata (se o DB permitir e for desejado)

            boolean deleted = journeyDAO.delete(id);
            if (deleted) {
                LOGGER.log(Level.INFO, "Jornada com ID {0} deletada com sucesso.", id);
            } else {
                LOGGER.log(Level.WARNING, "Falha ao deletar jornada com ID {0}.", id);
            }
            return deleted;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro de SQL ao deletar jornada: " + e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar a jornada. Tente novamente mais tarde.", e);
        }
    }
}
