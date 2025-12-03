package com.compliancesys.dao;

import com.compliancesys.model.TimeRecord; // Importa a classe TimeRecord.
import java.sql.SQLException;              // Importa para lidar com exceções SQL.
import java.time.LocalDate;                // Importa para usar LocalDate.
import java.util.List;                     // Importa para usar List.
import java.util.Optional;                 // Importa para usar Optional.

/**
 * Interface para operações de acesso a dados da entidade TimeRecord.
 * Define os métodos CRUD e de busca para TimeRecord.
 */
public interface TimeRecordDAO {

    /**
     * Insere um novo registro de ponto no banco de dados.
     * @param timeRecord Objeto TimeRecord a ser inserido.
     * @return O ID do registro de ponto inserido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int insert(TimeRecord timeRecord) throws SQLException;

    /**
     * Busca um registro de ponto pelo seu ID.
     * @param id ID do registro de ponto.
     * @return Um Optional contendo o TimeRecord se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<TimeRecord> findById(int id) throws SQLException;

    /**
     * Retorna todos os registros de ponto.
     * @return Uma lista de todos os registros de ponto.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findAll() throws SQLException;

    /**
     * Busca registros de ponto por ID de motorista.
     * @param driverId ID do motorista.
     * @return Uma lista de registros de ponto para o motorista especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findByDriverId(int driverId) throws SQLException;

    /**
     * Busca registros de ponto por ID de motorista e data.
     * @param driverId ID do motorista.
     * @param date Data dos registros.
     * @return Uma lista de registros de ponto para o motorista e data especificados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException;

    /**
     * Atualiza os dados de um registro de ponto existente.
     * @param timeRecord Objeto TimeRecord com os dados atualizados.
     * @return true se o registro foi atualizado, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(TimeRecord timeRecord) throws SQLException;

    /**
     * Deleta um registro de ponto pelo seu ID.
     * @param id ID do registro de ponto a ser deletado.
     * @return true se o registro foi deletado, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
