package com.compliancesys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.TimeRecord;

/**
 * Interface de serviço para operações de negócio relacionadas a TimeRecord.
 */
public interface TimeRecordService {

    /**
     * Cria um novo registro de ponto.
     * @param timeRecord O registro de ponto a ser criado.
     * @return O registro de ponto criado com o ID gerado.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    TimeRecord createTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException;

    /**
     * Busca um registro de ponto pelo ID.
     * @param id O ID do registro de ponto.
     * @return Um Optional contendo o registro de ponto se encontrado.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    Optional<TimeRecord> getTimeRecordById(int id) throws BusinessException, SQLException;

    /**
     * Busca todos os registros de ponto.
     * @return Uma lista de todos os registros de ponto.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    List<TimeRecord> getAllTimeRecords() throws BusinessException, SQLException;

    /**
     * Atualiza um registro de ponto existente.
     * @param timeRecord O registro de ponto com os dados atualizados.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    boolean updateTimeRecord(TimeRecord timeRecord) throws BusinessException, SQLException;

    /**
     * Exclui um registro de ponto pelo ID.
     * @param id O ID do registro de ponto a ser excluído.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    boolean deleteTimeRecord(int id) throws BusinessException, SQLException;

    /**
     * Busca registros de ponto de um motorista específico.
     * @param driverId O ID do motorista.
     * @return Uma lista de registros de ponto do motorista.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    List<TimeRecord> getTimeRecordsByDriverId(int driverId) throws BusinessException, SQLException;

    /**
     * Busca registros de ponto de um motorista em uma data específica.
     * @param driverId O ID do motorista.
     * @param date A data dos registros.
     * @return Uma lista de registros de ponto do motorista na data especificada.
     * @throws BusinessException Se houver erro de validação de negócio.
     * @throws SQLException Se houver erro de acesso ao banco de dados.
     */
    List<TimeRecord> getTimeRecordsByDriverIdAndDate(int driverId, LocalDate date) throws BusinessException, SQLException;
}