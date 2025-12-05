package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Journey;

/**
 * Interface para o Data Access Object (DAO) de Journey.
 * Define as operações CRUD e de busca específicas para jornadas de trabalho.
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
     * @return Um Optional contendo o Journey se encontrado, ou um Optional vazio.
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
     * Busca jornadas por ID de motorista.
     * @param driverId O ID do motorista.
     * @return Uma lista de jornadas associadas ao motorista.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByDriverId(int driverId) throws SQLException;

    /**
     * Busca uma jornada específica por ID de motorista e data.
     * @param driverId O ID do motorista.
     * @param journeyDate A data da jornada.
     * @return Um Optional contendo o Journey se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException;

    /**
     * Atualiza uma jornada existente no banco de dados.
     * @param journey O objeto Journey com os dados atualizados.
     * @return true se a jornada foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Journey journey) throws SQLException;

    /**
     * Deleta uma jornada pelo seu ID.
     * @param id O ID da jornada a ser deletada.
     * @return true se a jornada foi deletada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}