package com.compliancesys.dao;

import com.compliancesys.model.Driver; // Importa a classe Driver.
import java.sql.SQLException;           // Importa para lidar com exceções SQL.
import java.util.List;                  // Importa para usar List.
import java.util.Optional;              // Importa para usar Optional.

/**
 * Interface para operações de acesso a dados da entidade Driver.
 * Define os métodos CRUD para Driver.
 */
public interface DriverDAO {

    /**
     * Insere um novo motorista no banco de dados.
     * @param driver Objeto Driver a ser inserido.
     * @return O ID do motorista inserido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int insert(Driver driver) throws SQLException;

    /**
     * Busca um motorista pelo seu ID.
     * @param id ID do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> findById(int id) throws SQLException;

    /**
     * Busca um motorista pelo seu CPF.
     * @param cpf CPF do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> findByCpf(String cpf) throws SQLException;

    /**
     * Retorna todos os motoristas cadastrados.
     * @return Uma lista de todos os motoristas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Driver> findAll() throws SQLException;

    /**
     * Atualiza os dados de um motorista existente.
     * @param driver Objeto Driver com os dados atualizados.
     * @return true se o motorista foi atualizado, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Driver driver) throws SQLException;

    /**
     * Deleta um motorista pelo seu ID.
     * @param id ID do motorista a ser deletado.
     * @return true se o motorista foi deletado, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
