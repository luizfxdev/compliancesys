package com.compliancesys.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.model.Driver;

/**
 * Interface para o Data Access Object (DAO) de Driver.
 * Define as operações CRUD e de busca específicas para motoristas.
 */
public interface DriverDAO {

    /**
     * Insere um novo motorista no banco de dados.
     * @param driver O objeto Driver a ser inserido.
     * @return O ID gerado para o novo motorista. // CORRIGIDO: Retorna int
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    int create(Driver driver) throws SQLException; // CORRIGIDO: Retorna int

    /**
     * Busca um motorista pelo seu ID.
     * @param id O ID do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> findById(int id) throws SQLException;

    /**
     * Busca um motorista pelo seu CPF.
     * @param cpf O CPF do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> findByCpf(String cpf) throws SQLException;

    /**
     * Busca um motorista pelo seu número de CNH.
     * @param licenseNumber O número da CNH do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber) throws SQLException;

    /**
     * Busca todos os motoristas.
     * @return Uma lista de todos os motoristas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Driver> findAll() throws SQLException;

    /**
     * Atualiza um motorista existente no banco de dados.
     * @param driver O objeto Driver com os dados atualizados.
     * @return true se o motorista foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean update(Driver driver) throws SQLException;

    /**
     * Deleta um motorista pelo seu ID.
     * @param id O ID do motorista a ser deletado.
     * @return true se o motorista foi deletado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean delete(int id) throws SQLException;
}
