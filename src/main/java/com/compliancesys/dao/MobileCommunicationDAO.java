package com.compliancesys.dao;

import com.compliancesys.model.MobileCommunication;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface para o Data Access Object (DAO) de MobileCommunication.
 * Define as operações CRUD e de busca específicas para comunicações móveis.
 */
public interface MobileCommunicationDAO {

    /**
     * Insere uma nova comunicação móvel no banco de dados.
     * @param communication O objeto MobileCommunication a ser inserido.
     * @return O ID gerado para a nova comunicação móvel.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(MobileCommunication communication) throws SQLException;

    /**
     * Busca uma comunicação móvel pelo seu ID.
     * @param id O ID da comunicação móvel.
     * @return Um Optional contendo a MobileCommunication se encontrada, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<MobileCommunication> findById(int id) throws SQLException;

    /**
     * Busca todas as comunicações móveis.
     * @return Uma lista de todas as comunicações móveis.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> findAll() throws SQLException;

    /**
     * Busca comunicações móveis por ID de motorista.
     * @param driverId O ID do motorista.
     * @return Uma lista de comunicações móveis associadas ao motorista.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> findByDriverId(int driverId) throws SQLException;

    /**
     * Busca comunicações móveis por ID de registro de ponto.
     * @param recordId O ID do registro de ponto.
     * @return Uma lista de comunicações móveis associadas ao registro de ponto.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<MobileCommunication> findByRecordId(int recordId) throws SQLException;

    /**
     * Atualiza uma comunicação móvel existente no banco de dados.
     * @param communication O objeto MobileCommunication com os dados atualizados.
     * @return true se a comunicação móvel foi atualizada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(MobileCommunication communication) throws SQLException;

    /**
     * Deleta uma comunicação móvel pelo seu ID.
     * @param id O ID da comunicação móvel a ser deletada.
     * @return true se a comunicação móvel foi deletada com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
