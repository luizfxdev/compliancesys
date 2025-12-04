package com.compliancesys.service;

import com.compliancesys.model.MobileCommunication;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface para a camada de serviço da entidade MobileCommunication.
 * Define as operações de negócio para gerenciar registros de comunicação móvel.
 */
public interface MobileCommunicationService {

    /**
     * Registra uma nova comunicação móvel no sistema.
     * @param communication Objeto MobileCommunication a ser registrado.
     * @return O ID da comunicação móvel registrada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados da comunicação forem inválidos.
     */
    int registerMobileCommunication(MobileCommunication communication) throws SQLException, IllegalArgumentException;

    /**
     * Busca uma comunicação móvel pelo seu ID.
     * @param commId ID da comunicação móvel.
     * @return Um Optional contendo o MobileCommunication se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<MobileCommunication> getMobileCommunicationById(int commId) throws SQLException;

    /**
     * Busca comunicações móveis por ID de registro de ponto (TimeRecord).
     * @param recordId ID do registro de ponto.
     * @return Uma lista de comunicações móveis para o registro de ponto especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> getMobileCommunicationsByRecordId(int recordId) throws SQLException;

    /**
     * Retorna uma lista de todas as comunicações móveis registradas.
     * @return Uma lista de todas as comunicações móveis.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> getAllMobileCommunications() throws SQLException;

    /**
     * Atualiza as informações de uma comunicação móvel existente.
     * @param communication Objeto MobileCommunication com os dados atualizados.
     * @return true se a comunicação foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados da comunicação forem inválidos.
     */
    boolean updateMobileCommunication(MobileCommunication communication) throws SQLException, IllegalArgumentException;

    /**
     * Remove uma comunicação móvel do sistema pelo seu ID.
     * @param commId ID da comunicação móvel a ser removida.
     * @return true se a comunicação foi removida com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteMobileCommunication(int commId) throws SQLException;
}
