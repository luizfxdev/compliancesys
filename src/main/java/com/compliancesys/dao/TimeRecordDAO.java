package com.compliancesys.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.TimeRecord;

/**
 * Interface para operações de acesso a dados de registros de ponto (TimeRecord).
 */
public interface TimeRecordDAO {
    
    /**
     * Cria um novo registro de ponto no banco de dados.
     * @param timeRecord O objeto TimeRecord a ser criado.
     * @return O ID gerado para o novo registro de ponto.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(TimeRecord timeRecord) throws SQLException;

    /**
     * Busca um registro de ponto pelo seu ID.
     * @param id O ID do registro de ponto.
     * @return Um Optional contendo o TimeRecord se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<TimeRecord> findById(int id) throws SQLException;

    /**
     * Busca todos os registros de ponto.
     * @return Uma lista de todos os TimeRecords.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findAll() throws SQLException;

    /**
     * Busca registros de ponto por ID de motorista.
     * @param driverId O ID do motorista.
     * @return Uma lista de TimeRecords para o motorista especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findByDriverId(int driverId) throws SQLException;

    /**
     * Busca registros de ponto por ID de motorista e data.
     * @param driverId O ID do motorista.
     * @param date A data dos registros.
     * @return Uma lista de TimeRecords para o motorista e data especificados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> findByDriverIdAndDate(int driverId, LocalDate date) throws SQLException;

    /**
     * Atualiza um registro de ponto existente no banco de dados.
     * @param timeRecord O objeto TimeRecord com os dados atualizados.
     * @return true se o registro de ponto foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(TimeRecord timeRecord) throws SQLException;

    /**
     * Deleta um registro de ponto pelo seu ID.
     * @param id O ID do registro de ponto a ser deletado.
     * @return true se o registro de ponto foi deletado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}