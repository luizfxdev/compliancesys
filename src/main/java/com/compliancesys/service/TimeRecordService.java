package com.compliancesys.service;

import com.compliancesys.model.TimeRecord;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de serviço da entidade TimeRecord.
 * Define as operações de negócio para gerenciar registros de ponto.
 */
public interface TimeRecordService {

    /**
     * Registra um novo ponto para um motorista.
     * @param timeRecord Objeto TimeRecord a ser registrado.
     * @return O ID do registro de ponto criado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados do registro de ponto forem inválidos.
     */
    int registerTimeRecord(TimeRecord timeRecord) throws SQLException, IllegalArgumentException;

    /**
     * Busca um registro de ponto pelo seu ID.
     * @param recordId ID do registro de ponto.
     * @return Um Optional contendo o TimeRecord se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<TimeRecord> getTimeRecordById(int recordId) throws SQLException;

    /**
     * Retorna uma lista de todos os registros de ponto.
     * @return Uma lista de todos os registros de ponto.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> getAllTimeRecords() throws SQLException;

    /**
     * Busca registros de ponto por ID de motorista.
     * @param driverId ID do motorista.
     * @return Uma lista de registros de ponto para o motorista especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws SQLException;

    /**
     * Busca registros de ponto por ID de motorista e data.
     * @param driverId ID do motorista.
     * @param date Data dos registros.
     * @return Uma lista de registros de ponto para o motorista e data especificados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws SQLException;

    /**
     * Atualiza as informações de um registro de ponto existente.
     * @param timeRecord Objeto TimeRecord com os dados atualizados.
     * @return true se o registro foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados do registro de ponto forem inválidos.
     */
    boolean updateTimeRecord(TimeRecord timeRecord) throws SQLException, IllegalArgumentException;

    /**
     * Remove um registro de ponto do sistema pelo seu ID.
     * @param recordId ID do registro de ponto a ser removido.
     * @return true se o registro foi removido com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteTimeRecord(int recordId) throws SQLException;
}
