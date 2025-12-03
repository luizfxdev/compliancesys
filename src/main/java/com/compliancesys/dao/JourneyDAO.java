package com.compliancesys.dao;

import com.compliancesys.model.Journey; // Importa a classe Journey.
import java.sql.SQLException;           // Importa para lidar com exceções SQL.
import java.time.LocalDate;             // Importa para usar LocalDate.
import java.util.List;                  // Importa para usar List.
import java.util.Optional;              // Importa para usar Optional.

/**
 * Interface para operações de acesso a dados da entidade Journey.
 * Define os métodos CRUD e de busca para Journey.
 */
public interface JourneyDAO {

    /**
     * Insere uma nova jornada no banco de dados.
     * @param journey Objeto Journey a ser inserido.
     * @return O ID da jornada inserida.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int insert(Journey journey) throws SQLException;

    /**
     * Busca uma jornada pelo seu ID.
     * @param id ID da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> findById(int id) throws SQLException;

    /**
     * Busca uma jornada pelo ID do motorista e data da jornada.
     * @param driverId ID do motorista.
     * @param journeyDate Data da jornada.
     * @return Um Optional contendo a Journey se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Journey> findByDriverIdAndDate(int driverId, LocalDate journeyDate) throws SQLException;

    /**
     * Retorna todas as jornadas registradas.
     * @return Uma lista de todas as jornadas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findAll() throws SQLException;

    /**
     * Retorna todas as jornadas de um motorista específico.
     * @param driverId ID do motorista.
     * @return Uma lista de jornadas para o motorista especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Journey> findByDriverId(int driverId) throws SQLException;

    /**
     * Atualiza os dados de uma jornada existente.
     * @param journey Objeto Journey com os dados atualizados.
     * @return true se a jornada foi atualizada, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Journey journey) throws SQLException;

    /**
     * Deleta uma jornada pelo seu ID.
     * @param id ID da jornada a ser deletada.
     * @return true se a jornada foi deletada, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
