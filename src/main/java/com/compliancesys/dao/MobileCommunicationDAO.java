package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.MobileCommunication;

/**
 * Interface para a camada de acesso a dados (DAO) da entidade MobileCommunication.
 * Define as operações CRUD (Create, Read, Update, Delete) e outras buscas
 * específicas para a entidade MobileCommunication no banco de dados.
 */
public interface MobileCommunicationDAO {

    /**
     * Cria um novo registro de comunicação móvel no banco de dados.
     * @param communication Objeto MobileCommunication a ser criado.
     * @return O ID do registro de comunicação móvel criado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(MobileCommunication communication) throws SQLException;

    /**
     * Busca um registro de comunicação móvel pelo seu ID.
     * @param id ID do registro de comunicação móvel.
     * @return Um Optional contendo o MobileCommunication se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<MobileCommunication> findById(int id) throws SQLException;

    /**
     * Retorna uma lista de todos os registros de comunicação móvel.
     * @return Uma lista de todos os registros de comunicação móvel.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> findAll() throws SQLException;

    /**
     * Busca registros de comunicação móvel associados a um ID de motorista.
     * Este método foi adicionado para resolver o erro de compilação em MobileCommunicationDAOImpl.
     * @param driverId ID do motorista.
     * @return Uma lista de registros de comunicação móvel para o motorista especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> findByDriverId(int driverId) throws SQLException; // <--- LINHA ADICIONADA

    /**
     * Busca registros de comunicação móvel associados a um ID de registro de ponto.
     * @param recordId ID do registro de ponto.
     * @return Uma lista de registros de comunicação móvel para o registro de ponto especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> findByRecordId(int recordId) throws SQLException;

    /**
     * Atualiza as informações de um registro de comunicação móvel existente.
     * @param communication Objeto MobileCommunication com os dados atualizados.
     * @return true se o registro foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(MobileCommunication communication) throws SQLException;

    /**
     * Remove um registro de comunicação móvel do banco de dados pelo seu ID.
     * @param id ID do registro de comunicação móvel a ser removido.
     * @return true se o registro foi removido com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
