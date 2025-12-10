package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Journey;

/**
 * Interface para o Data Access Object (DAO) de Journey.
 * Define as operações CRUD e de busca específicas para jornadas.
 */
public interface JourneyDAO {

    /**
     * Insere uma nova jornada no banco de dados.
     * @param journey O objeto Journey a ser inserido.
     * @return O ID gerado para a nova jornada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(Journey journey) throws SQLException;

    /**
     * Busca uma jornada pelo seu ID.
     * @param id O ID da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> findById(int id) throws SQLException;

    /**
     * Busca todas as jornadas.
     * @return Uma lista de todas as jornadas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findAll() throws SQLException;

    /**
     * Atualiza uma jornada existente no banco de dados.
     * @param journey O objeto Journey com os dados atualizados.
     * @return true se a jornada foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Journey journey) throws SQLException;

    /**
     * Exclui uma jornada do banco de dados pelo seu ID.
     * @param id O ID da jornada a ser excluída.
     * @return true se a jornada foi excluída com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;

    /**
     * Busca jornadas de um motorista específico.
     * @param driverId O ID do motorista.
     * @return Uma lista de jornadas do motorista.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByDriverId(int driverId) throws SQLException;

    /**
     * Busca uma jornada específica de um motorista em uma determinada data.
     * @param driverId O ID do motorista.
     * @param journeyDate A data da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException;

    /**
     * Busca jornadas de um veículo específico.
     * @param vehicleId O ID do veículo.
     * @return Uma lista de jornadas do veículo.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByVehicleId(int vehicleId) throws SQLException;

    /**
     * Busca uma jornada específica de um veículo em uma determinada data.
     * @param vehicleId O ID do veículo.
     * @param journeyDate A data da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> findByVehicleIdAndDate(int vehicleId, LocalDate journeyDate) throws SQLException;

    /**
     * Busca jornadas de um motorista dentro de um período de datas.
     * @param driverId O ID do motorista.
     * @param startDate Data de início do período.
     * @param endDate Data de fim do período.
     * @return Uma lista de jornadas do motorista dentro do período especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByDriverIdAndDateRange(int driverId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Busca jornadas de um veículo dentro de um período de datas.
     * @param vehicleId O ID do veículo.
     * @param startDate Data de início do período.
     * @param endDate Data de fim do período.
     * @return Uma lista de jornadas do veículo dentro do período especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByVehicleIdAndDateRange(int vehicleId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Busca jornadas por status.
     * @param status O status da jornada (ex: "IN_PROGRESS", "COMPLETED").
     * @return Uma lista de jornadas com o status especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByStatus(String status) throws SQLException;

    /**
     * Busca jornadas por status e dentro de um período de datas.
     * @param status O status da jornada.
     * @param startDate Data de início do período.
     * @param endDate Data de fim do período.
     * @return Uma lista de jornadas com o status e dentro do período especificados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByStatusAndDateRange(String status, LocalDate startDate, LocalDate endDate) throws SQLException; // ESTE MÉTODO DEVE ESTAR AQUI

    // === MÉTODOS DEFAULT (ALIASES) ===

    /**
     * Busca uma jornada pelo seu ID (alias para findById).
     * @param id O ID da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    default Optional<Journey> getById(int id) throws SQLException {
        return findById(id);
    }

    /**
     * Busca todas as jornadas (alias para findAll).
     * @return Uma lista de todas as jornadas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    default List<Journey> getAll() throws SQLException {
        return findAll();
    }

    /**
     * Busca jornadas de um motorista (alias para findByDriverId).
     * @param driverId O ID do motorista.
     * @return Uma lista de jornadas do motorista.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    default List<Journey> getJourneysByDriverId(int driverId) throws SQLException {
        return findByDriverId(driverId);
    }

    /**
     * Busca uma jornada por motorista e data (alias para findByDriverIdAndDate).
     * @param driverId O ID do motorista.
     * @param journeyDate A data da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    default Optional<Journey> getJourneyByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException {
        return findByDriverIdAndDate(driverId, journeyDate);
    }
}
