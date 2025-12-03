package com.compliancesys.service;

import com.compliancesys.model.Driver; // Garante que o POJO Driver está sendo importado.
import java.sql.SQLException;           // Garante que SQLException está sendo importado.
import java.util.List;                  // Garante que List está sendo importado.
import java.util.Optional;              // Garante que Optional está sendo importado.

/**
 * Interface para a camada de serviço da entidade Driver.
 * Define as operações de negócio para gerenciar motoristas.
 */
public interface DriverService {

    /**
     * Registra um novo motorista no sistema.
     * Realiza validações de negócio antes de persistir o motorista.
     * @param driver Objeto Driver a ser registrado.
     * @return O ID do motorista registrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados do motorista forem inválidos.
     */
    int registerDriver(Driver driver) throws SQLException, IllegalArgumentException;

    /**
     * Busca um motorista pelo seu ID.
     * @param driverId ID do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> getDriverById(int driverId) throws SQLException;

    /**
     * Busca um motorista pelo seu CPF.
     * @param cpf CPF do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> getDriverByCpf(String cpf) throws SQLException;

    /**
     * Retorna uma lista de todos os motoristas registrados.
     * @return Uma lista de todos os motoristas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Driver> getAllDrivers() throws SQLException;

    /**
     * Atualiza as informações de um motorista existente.
     * Realiza validações de negócio antes de atualizar.
     * @param driver Objeto Driver com os dados atualizados.
     * @return true se o motorista foi atualizado com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se os dados do motorista forem inválidos.
     */
    boolean updateDriver(Driver driver) throws SQLException, IllegalArgumentException;

    /**
     * Remove um motorista do sistema pelo seu ID.
     * @param driverId ID do motorista a ser removido.
     * @return true se o motorista foi removido com sucesso, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteDriver(int driverId) throws SQLException;
}
