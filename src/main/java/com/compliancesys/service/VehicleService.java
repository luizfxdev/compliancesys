package com.compliancesys.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.compliancesys.exception.BusinessException;
import com.compliancesys.model.Vehicle;

/**
 * Interface para o serviço de gerenciamento de veículos.
 * Define as operações de negócio relacionadas aos veículos.
 */
public interface VehicleService {

    /**
     * Registra um novo veículo no sistema.
     * @param vehicle O objeto Vehicle a ser registrado.
     * @return O objeto Vehicle registrado, incluindo o ID gerado. // CORRIGIDO: Retorna Vehicle
     * @throws BusinessException Se houver uma regra de negócio violada.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Vehicle registerVehicle(Vehicle vehicle) throws BusinessException, SQLException; // CORRIGIDO: Retorna Vehicle

    /**
     * Busca um veículo pelo seu ID.
     * @param vehicleId O ID do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws BusinessException Se o ID for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> getVehicleById(int vehicleId) throws BusinessException, SQLException;

    /**
     * Busca um veículo pela sua placa.
     * @param plate A placa do veículo.
     * @return Um Optional contendo o Vehicle se encontrado, ou um Optional vazio.
     * @throws BusinessException Se a placa for inválida.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Optional<Vehicle> getVehicleByPlate(String plate) throws BusinessException, SQLException;

    /**
     * Busca todos os veículos.
     * @return Uma lista de todos os veículos.
     * @throws BusinessException Se ocorrer um erro de negócio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    List<Vehicle> getAllVehicles() throws BusinessException, SQLException;

    /**
     * Atualiza os dados de um veículo existente.
     * @param vehicle O objeto Vehicle com os dados atualizados.
     * @return O objeto Vehicle atualizado. // CORRIGIDO: Retorna Vehicle
     * @throws BusinessException Se houver uma regra de negócio violada ou o veículo não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    Vehicle updateVehicle(Vehicle vehicle) throws BusinessException, SQLException; // CORRIGIDO: Retorna Vehicle

    /**
     * Deleta um veículo pelo seu ID.
     * @param vehicleId O ID do veículo a ser deletado.
     * @return true se o veículo foi deletado com sucesso, false caso contrário.
     * @throws BusinessException Se o ID for inválido ou o veículo não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    boolean deleteVehicle(int vehicleId) throws BusinessException, SQLException;
}
