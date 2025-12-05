package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Driver;

/**
 * Interface para o serviço de gerenciamento de motoristas.
 * Define as operações de negócio relacionadas aos motoristas.
 */
public interface DriverService {

    /**
     * Registra um novo motorista no sistema.
     * @param driver O objeto Driver a ser registrado.
     * @return O objeto Driver registrado, incluindo o ID gerado. // CORRIGIDO: Retorna Driver
     * @throws BusinessException Se houver uma regra de negócio violada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Driver registerDriver(Driver driver) throws BusinessException, SQLException; // CORRIGIDO: Retorna Driver

    /**
     * Busca um motorista pelo seu ID.
     * @param driverId O ID do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws BusinessException Se o ID for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> getDriverById(int driverId) throws BusinessException, SQLException;

    /**
     * Busca um motorista pelo seu CPF.
     * @param cpf O CPF do motorista.
     * @return Um Optional contendo o Driver se encontrado, ou um Optional vazio.
     * @throws BusinessException Se o CPF for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Driver> getDriverByCpf(String cpf) throws BusinessException, SQLException;

    /**
     * Busca todos os motoristas.
     * @return Uma lista de todos os motoristas.
     * @throws BusinessException Se ocorrer um erro de negócio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Driver> getAllDrivers() throws BusinessException, SQLException;

    /**
     * Atualiza os dados de um motorista existente.
     * @param driver O objeto Driver com os dados atualizados.
     * @return O objeto Driver atualizado. // CORRIGIDO: Retorna Driver
     * @throws BusinessException Se houver uma regra de negócio violada ou o motorista não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Driver updateDriver(Driver driver) throws BusinessException, SQLException; // CORRIGIDO: Retorna Driver

    /**
     * Deleta um motorista pelo seu ID.
     * @param driverId O ID do motorista a ser deletado.
     * @return true se o motorista foi deletado com sucesso, false caso contrário.
     * @throws BusinessException Se o ID for inválido ou o motorista não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteDriver(int driverId) throws BusinessException, SQLException;
}
